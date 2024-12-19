package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDAO;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	private ProjectDAO projectDao = new ProjectDAO();
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(
				() -> new NoSuchElementException(
					"Project with project ID=" + projectId	
					+" does not exist."));
	}
	public void modifyProjectDetails(Project project) {
		// Confirm that UPDATE for database was performed successfully:
		if(!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID="
					+ project.getProjectId() + " does not exist.");
		
		}
	}

}
