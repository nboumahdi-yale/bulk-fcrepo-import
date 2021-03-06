package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.persistence.dao.HydraPublishDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
@SuppressWarnings("unchecked")
public class HydraPublishView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private HydraPublishDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

}


