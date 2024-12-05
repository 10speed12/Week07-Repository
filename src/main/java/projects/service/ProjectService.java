package projects.service;

import projects.dao.ProjectDAO;
import projects.entity.Project;

public class ProjectService {
	private ProjectDAO projectDao = new ProjectDAO();
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

}
