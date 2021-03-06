package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.MetadataRoles;
import edu.yale.library.ladybird.entity.UserProjectField;
import edu.yale.library.ladybird.entity.UserProjectFieldBuilder;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldDAO;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
@SuppressWarnings("unchecked")
@ManagedBean
@ViewScoped
public class AssignProjectFieldView extends AbstractView implements Serializable {

    private static final long serialVersionUID = 6223995917417414208L;

    private final Logger logger = LoggerFactory.getLogger(AssignProjectFieldView.class);

    //TODO distinguish between project role and field role?
    private MetadataRoles metadataRoles;

    private int fieldDefintion;

    @Inject
    private UserProjectFieldDAO userProjectFieldDAO;

    @PostConstruct
    public void init() {
        initFields();
    }

    public MetadataRoles[] getRoles() {
        return MetadataRoles.values();
    }

    public MetadataRoles getMetadataRoles() {
        return metadataRoles;
    }

    public void setMetadataRoles(MetadataRoles metadataRoles) {
        this.metadataRoles = metadataRoles;
    }

    //TODO use a converter (for fdid)
    //TODO update if the value already exists
    public String save() {
        int userId = Integer.parseInt(Faces.getRequestParameter("userId"));
        int projectId = Integer.parseInt(Faces.getRequestParameter("projectId"));

        logger.debug("Saving project id={} with field={} with role={} for user={}", projectId, fieldDefintion,
                metadataRoles.name(), userId);

        final UserProjectField userProjectField = new UserProjectFieldBuilder().
                setProjectId(projectId).
                setUserId(userId).
                setFdid(fieldDefintion).
                setRole(metadataRoles.name()).
                setDate(new Date()).
                createUserProjectField();
        try {
            //logger.debug("Saving entity={}", userProjectField);
            userProjectFieldDAO.save(userProjectField);
            return ok();
        } catch (Exception e) {
            logger.error("Exception saving project role", e);
            return fail();
        }
    }

    /**
     * Redirects to save page
     *
     * @return page to redirect to
     */
    public String assign() {
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        int userId = Integer.parseInt(params.get("userId"));

        //Reads either the originating get request parameter or datatable id
        // TODO The latter case is to make page work where there's not project_id=n in the url.
        int projectId;
        if (params.get("projectId") != null && !params.get("projectId").isEmpty()) {
            projectId = Integer.parseInt(params.get("projectId"));
        } else {
            projectId = Integer.parseInt(params.get("dataTableProjectId"));
        }
        return getRedirectWithParam(NavigationUtil.USER_METADATA_ACCESS_PAGE, userId, projectId);
    }

    /**
     * Re-direct to edit page
     */
    //TODO clean up params
    public String redirectEdit() {
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        int userId = Integer.parseInt(params.get("userId"));

        //Reads either the originating get request parameter or datatable id
        // TODO The latter case is to make page work where there's not project_id=n in the url.
        int projectId;
        if (params.get("projectId") != null && !params.get("projectId").isEmpty()) {
            projectId = Integer.parseInt(params.get("projectId"));
        } else {
            projectId = Integer.parseInt(params.get("dataTableProjectId"));
        }
        return getRedirectWithParam(NavigationUtil.USER_METADATA_ACCESS_EDIT_PAGE, userId, projectId);
    }

    private String getRedirectWithParam(String page, int userId, int projectId) {
        return page + "?faces-redirect=true&user_id=" + userId + "&project_id=" + projectId;
    }

    public int getFieldDefintion() {
        return fieldDefintion;
    }

    public void setFieldDefintion(int fieldDefintion) {
        this.fieldDefintion = fieldDefintion;
    }
}
