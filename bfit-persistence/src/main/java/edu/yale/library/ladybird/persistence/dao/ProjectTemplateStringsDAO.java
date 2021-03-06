package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ProjectTemplateStrings;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface ProjectTemplateStringsDAO extends GenericDAO<ProjectTemplateStrings, Integer> {

    ProjectTemplateStrings findByFdidAndTemplateId(int fdid, int templateId);

    List<ProjectTemplateStrings> findByTemplateId(int templateId);

}

