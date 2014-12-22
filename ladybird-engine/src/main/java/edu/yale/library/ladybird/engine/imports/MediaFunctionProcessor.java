package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.file.ImageMagickProcessor;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Column;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Row;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.ImportFile;
import edu.yale.library.ladybird.entity.ImportFileBuilder;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ObjectFileBuilder;
import edu.yale.library.ladybird.persistence.dao.ImportFileDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportFileHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.Integer.parseInt;
import static org.slf4j.LoggerFactory.getLogger;

public class MediaFunctionProcessor {

    private Logger logger = getLogger(this.getClass());

    private String rootPath = "";

    private String projectDir = "";

    private static String defaultImage = "no-image-found.jpg";

    private final ImportFileDAO importFileDAO = new ImportFileHibernateDAO();

    private final ImageMagickProcessor imgMagick = new ImageMagickProcessor();

    private final ObjectFileDAO objectFileDAO = new ObjectFileHibernateDAO();

    private final String PATH_PREFIX =  rootPath + File.separator + projectDir + File.separator;

    private final String OUT_PATH_PREFIX = rootPath + File.separator + projectDir + File.separator + "exports"
            + File.separator;

    /**
     * Process images and writes to import file and object file
     *
     * @param importEntityValue import EntityValue
     * @throws IOException if any single conversion fails
     */
    @SuppressWarnings("unchecked")
    public void convert(final int importId, final ImportEntityValue importEntityValue) throws IOException {
        final String blankFileName = "N/A";

        final int userId = 1; //TODO check this
        final List<Row> rowList = importEntityValue.getContentRows();

        logger.debug("[start] converting media for import id={} rowlist size={}", importId, rowList.size());

        final long timeInConversion = System.currentTimeMillis();

        final String PATH_PREFIX =  rootPath + File.separator + projectDir + File.separator;
        final String OUT_PATH_PREFIX = rootPath + File.separator + projectDir + File.separator + "exports"
                + File.separator;

        for (int i = 0; i < rowList.size(); i++) {

            if (i % 100 == 0) {
                logger.debug("Converted media, so far={} for importId={}", i, importId);
            }

            final Column<String> f3 = importEntityValue.getRowFieldColumn(FunctionConstants.F3, i);
            checkState(f3.getField().getName().equals(FunctionConstants.F3.getName()), "Found wrong F3 col");

            final String f3Col = f3.getValue();
            logger.trace("Evaluating f3={}", f3Col);

            final Column<String> f1 = importEntityValue.getRowFieldColumn(FunctionConstants.F1, i);
            checkState(f1.getField().getName().equals(FunctionConstants.F1.getName()), "Found wrong F1 col");
            final int oid = parseInt(f1.getValue());

            // Update import file and convert image (plus a thumbnail)
            // or, if no image found: update dao with blank image found on project folder & skip ImageMagick step.
            try {

                final File file = new File(PATH_PREFIX + f3Col);

                logger.trace("Eval file={}", file.getAbsolutePath());

                if (file.exists() && !file.isDirectory()) {
                    final ImportFile imFile = getBuilder().importId(importId).fileLocation(f3Col).oid(oid).create();
                    importFileDAO.save(imFile);

                    final String from = MediaFormat.TIFF.toString();
                    final String to = MediaFormat.JPEG.toString();
                    final String outputFilePath = asFormat(OUT_PATH_PREFIX + f3Col, from, to);
                    final byte[] thumbnailBytes = convertImage(f3Col, outputFilePath, MediaFormat.TIFF, MediaFormat.JPEG, oid);

                    final ObjectFile exObjectFile = objectFileDAO.findByOid(oid);

                    if (exObjectFile == null) {
                        final ObjectFile objectFile = new ObjectFileBuilder().setDate(new Date()).setFilePath(outputFilePath)
                                .setFileExt(MediaFormat.JPEG.toString()).setOid(oid).setUserId(userId).setFileLabel(f3Col)
                                .setThumbnail(thumbnailBytes).setFileName(f3Col.replace(from, to)).createObjectFile();

                        objectFileDAO.save(objectFile);
                    } else {
                        exObjectFile.setFileName(f3Col.replace(from, to));
                        exObjectFile.setFileExt(MediaFormat.JPEG.toString());
                        exObjectFile.setFilePath(outputFilePath);
                        exObjectFile.setDate(new Date());
                        exObjectFile.setThumbnail(thumbnailBytes);
                        exObjectFile.setFileLabel(f3Col);

                        objectFileDAO.saveOrUpdateItem(exObjectFile);
                    }
                } else {
                    final ImportFile imFile = getBuilder().importId(importId).oid(oid)
                            .fileLocation(PATH_PREFIX + defaultImage).create();
                    importFileDAO.save(imFile);

                    byte[] thumbnail = getDefaultThumbnail();

                    if (thumbnail == null || thumbnail.length == 0) {
                        logger.error("Null bytes for thumbnail for oid={}", oid);
                    }

                    final ObjectFile objectFile = new ObjectFileBuilder().setDate(new Date()).setFilePath(PATH_PREFIX + defaultImage)
                            .setFileExt(MediaFormat.JPEG.toString()).setOid(oid).setUserId(userId).setFileLabel(blankFileName)
                            .setThumbnail(thumbnail).setFileName(blankFileName).createObjectFile();

                    objectFileDAO.save(objectFile);
                }
            } catch (IOException e) {
                logger.error("Error/warning persisting entity", e);
                throw e;
            }
        }
        logger.debug("[end] conversion complete in={}",
                DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - timeInConversion));

    }

    /**
     * Converts image and updates object file table
     *
     * @param fileName filename
     * @param fromExt  ext to convert to
     * @param toExt    ext to convert to
     * @param oid      oid
     */
    private byte[] convertImage(final String fileName, final String outputFilePath, final MediaFormat fromExt,
                                final MediaFormat toExt, int oid)
            throws IOException {
        // 1. convert
        final String inputFilePath = PATH_PREFIX + fileName;
        try {
            imgMagick.toFormat(inputFilePath, outputFilePath);

            logger.trace("Converted image to={}", outputFilePath);
        } catch (Exception e) {
            logger.trace("Error/warning converting for oid={}", oid); //N.B. ignore errors and warnings
            logger.trace("Error", e);
        }

        //1b. convert to thumbnail
        final String thumbnailPath = asFormat(OUT_PATH_PREFIX + fileName, fromExt.toString(), MediaFormat.THUMBNAIL.toString());

        try {
            imgMagick.toThumbnailFormat(outputFilePath, thumbnailPath);

            logger.trace("Converted image to thumbnail={}", thumbnailPath);
        } catch (Exception e) {
            logger.trace("Error/warning converting to thumbnail for oid={}", oid); //N.B. ignore errors and warnings
            logger.trace("Error", e);
        }

        //2. prepare object file and persist. If the thumbnail is not found, try a default. If that fails, throw.
        byte[] thumbnail;
        try {
            thumbnail = getBytes(thumbnailPath);

            if (thumbnail == null || thumbnail.length == 0) {
                logger.error("Null bytes for thumbnail for oid={}", oid);
            }

        } catch (IOException e) {
            logger.error("Thumbnail image not found for oid={}", oid, e); //this means a conversion error
            try {
                thumbnail = getBytes(ImageMagickProcessor.getBlankImagePath());

                if (thumbnail == null || thumbnail.length == 0) {
                    throw new IOException("No thumbnail for oid=" + oid);
                }
            } catch (IOException io) {
                logger.error("Error setting default thumbnail image for oid={}", oid, io);
                throw io;
            }
        }

        return thumbnail;
    }

    /**
     * Update dao with blank image found on project folder. Skip ImageMagick image conversion.
     *
     * @param importEntityValue import EntityValue
     * @throws Exception if any single conversion fails
     */
    @SuppressWarnings("unchecked")
    public void createObjectFiles(final int importId, final ImportEntityValue importEntityValue) throws Exception {
        logger.debug("[start] Creating ObjectFiles with blank image references for importId={}", importId);
        final int userId = 1; //TODO check this
        final String nullFileName = "N/A";
        final byte[] thumbnail = getDefaultThumbnail();
        final ObjectFileBuilder objectFileBuilder = new ObjectFileBuilder();
        final ImportFileBuilder importFileBuilder = getBuilder();
        final Date currentDate = new Date();
        final String filePath = PATH_PREFIX + defaultImage;
        final String jpeg = MediaFormat.JPEG.toString();

        List<Row> rowList = importEntityValue.getContentRows();
        List<ImportFile> importFiles = new ArrayList<>();
        List<ObjectFile> objectFiles = new ArrayList<>();

        int oid;

        for (int i = 0; i < rowList.size(); i++) {
            final Column<String> f1 = importEntityValue.getRowFieldColumn(FunctionConstants.F1, i);
            checkState(f1.getField().getName().equals(FunctionConstants.F1.getName()), "Found wrong F1 col");
            oid = parseInt(f1.getValue());
            importFiles.add(importFileBuilder.importId(importId).oid(oid).fileLocation(filePath).create());
            objectFiles.add(objectFileBuilder.setDate(currentDate).setFilePath(filePath).setFileExt(jpeg)
                    .setOid(oid).setUserId(userId).setFileLabel(nullFileName).setThumbnail(thumbnail)
                    .setFileName(nullFileName).createObjectFile());
        }

        try {
            logger.trace("Saving importFile list of size={}", importFiles.size());
            importFileDAO.saveList(importFiles);
            logger.trace("Saving objectFiles list of size={}", objectFiles.size());
            objectFileDAO.saveList(objectFiles);
        } catch (Throwable e) {
            logger.error("Error/warning persisting importFile or objectFile", e);
            throw e;
        }
        logger.debug("[end] done creating ObjectFiles");
    }

    /**
     * Converts image and updates object file table
     */
    private byte[] getDefaultThumbnail() throws IOException {
        final String thumbnailPath = ImageMagickProcessor.getBlankImagePath();
        return getBytes(thumbnailPath);
    }

    private byte[] getBytes(String filePath) throws IOException {
        byte[] bytes;
        try {
            bytes = FileUtils.readFileToByteArray(new File(filePath));
        } catch (IOException e) {
            logger.error("Error getting bytes for projectDir={}", projectDir, e);
            throw e;
        }
        return bytes;
    }

    private static String asFormat(final String fileName, final String from, final String ext) {
        return fileName.replace(from, ext);
    }

    private enum MediaFormat {

        TIFF(".tif"),
        JPEG(".jpg"),
        JPEG2000(".jp2"),
        THUMBNAIL("-thumbnail.jpg");

        String name;

        private MediaFormat(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    public String getProjectDir() {
        return projectDir;
    }

    public MediaFunctionProcessor(String rootPath, String projectDir) {
        this.rootPath = rootPath;
        this.projectDir = projectDir;
    }

    private static ImportFileBuilder getBuilder() {
        return new ImportFileBuilder().date(new Date());
    }
}
