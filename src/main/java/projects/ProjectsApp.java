package projects;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

//import projects.dao.DbConnection;
import projects.exception.DbException;
import projects.service.ProjectService;
import projects.entity.*;


public class ProjectsApp {
	// @formatter:off
	private List<String> operations = List.of("1) Add a project"
			, "2) List projects"
			, "3) Select a project"
			, "4) Update project details"
			, "5) Delete a project");
	// @formatter:on
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	// Instance variable for currently selected project:
	private Project currProject;
	
	public static void main(String[] args) {
		// Create a new instance of the user menu:
		new ProjectsApp().processUserSelections();
		
	}
	
	private void processUserSelections() {
		boolean done = false;
		while(!done) {
			try {
				int selection = getUserSelection();
				switch(selection) {
					case -1:
						done = exitMenu();
						break;
					case 1:	
						createProject();
						break;
					case 2:	
						listProjects();
						break;
					case 3:
						selectProject();
						break;
					case 4:	
						updateProjectDetails();
						break;
					case 5:
						deleteProject();
						break;
					default:
						System.out.println("\n" + selection + " is not a valid selection. Try again.");
						break;
				}
			}catch(Exception e) {
				System.out.println("\nError: " + e + " Try again");
			}
			
		}
	}

	private int getUserSelection() {
		printOperations();
		
		Integer input = getIntInput("Enter a menu selection");
		// If input is null, return -1. Otherwise, return value of input:
		return Objects.isNull(input) ? -1 : input;
	}

	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		for(String s: operations) {
			// Print out current item in operations, indented with a tab:
			System.out.println("\t" + s);
		}
	}
	
	// Have a user create a new project and add it to the database:
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		int difficulty = getIntInput("Enter the project difficulty (1-5)");
		// Confirm that difficulty is between 1 and 5:
		while(difficulty < 1 || 5 < difficulty) {
			// Print error message to console:
			System.out.println("Error, "+difficulty+" is not a value between 1 and 5, inclusive.");
			// Call getIntInput again to re-acquire value for difficulty:
			difficulty = getIntInput("Enter the project difficulty (1-5)");
		}
		String notes = getStringInput("Enter the project notes");
		// Create new project instance:
		Project project = new Project();
		// Call all the necessary setter functions of project to set it's fields to the values previously entered by
		// the user:
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: "+dbProject);
		
	}
	
	private void listProjects() {
		// Retrieve all projects in the database as a list of Project items:
		List<Project> projects = projectService.fetchAllProjects();
		// Print contents of projects to console:
		System.out.println("\nProjects");
		projects.forEach(project -> System.out.println(
				"	" + project.getProjectId()
				+ ": "+ project.getProjectName()));
	}
	
	// Allow a user to "select" a project in the database where they can add materials, categories, or steps:
	private void selectProject() {
		// Obtain list of existent projects by calling listProjects:
		listProjects();
		// Obtain a valid projectId from user:
		Integer projectId = getIntInput("Enter a project ID to select a project: ");
		// Set currProject to null to un-select current project to avoid potential errors:
		currProject=null;
		// Set currProject to project in database with entered ID. An exception will be thrown if an invalid ID is entered:
		currProject = projectService.fetchProjectById(projectId);
		
		if(Objects.isNull(currProject)) {
			System.out.println("\nYou are not working on a project.");
		}else {
			System.out.println("\nYou are working with project: " + currProject);
		}
	}
	
	private void updateProjectDetails() {
		// Confirm that a project is currently being worked on:
		if(Objects.isNull(currProject)) {
			// Print error message if currProject is null:
			System.out.println("\nPlease select a project");
			return;
		}
			// Get new project name:
			String projectName = getStringInput("Enter the project name ["
					+ currProject.getProjectName() + "]");
			// Get new estimated hours:
			BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours ["
					+ currProject.getEstimatedHours() + "]");
			// Get new actual hours:
			BigDecimal actualHours = getDecimalInput("Enter the actual hours ["
					+ currProject.getActualHours() + "]");
			// Get new project difficulty:
			Integer difficulty = getIntInput("Enter the project difficulty (1-5) ["
					+currProject.getDifficulty() + "]");
			// Get new notes:
			String notes = getStringInput("Enter the project notes ["
					+ currProject.getNotes() + "]");
			// Create new Project to store results:
			Project project = new Project();
			// Set new projectId to currProject's project id value:
			project.setProjectId(currProject.getProjectId());
			// Set new project name value to updated form if projectName is not null, otherwise, leave as is:
			project.setProjectName(Objects.isNull(projectName) ? currProject.getProjectName() : projectName);
			// Set new estimated hours to updated form if estimatedHours is not null, otherwise, leave as is:
			project.setEstimatedHours(Objects.isNull(estimatedHours) ? currProject.getEstimatedHours() : estimatedHours);
			// Set new actual hours to updated form if actualHours is not null, otherwise, leave as is:
			project.setActualHours(Objects.isNull(actualHours) ? currProject.getActualHours() : actualHours);
			// Set new difficulty value to updated form if difficulty is not null, otherwise, leave as is:
			project.setDifficulty(Objects.isNull(difficulty) ? currProject.getDifficulty() : difficulty);
			// Set new notes value to updated form if notes is not null, otherwise, leave as is:
			project.setNotes(Objects.isNull(notes) ? currProject.getNotes() : notes);
			
			// Call projectService's modifyProjectDetails function to update database with the new information:
			projectService.modifyProjectDetails(project);
			// Reread the current project to reflect changes made:
			currProject= projectService.fetchProjectById(currProject.getProjectId());
		
	}
	
	private void deleteProject() {
		// Call listProjects to show valid options for deletion:
		listProjects();
		//Obtain id of project to be deleted:
		Integer deleteId = getIntInput("Enter ID of project to be deleted: ");
		// Call delete project function in projectService:
		projectService.deleteProject(deleteId);
		// Print success message to user:
		System.out.println("Project " + deleteId + " was deleted successfully.");
		// If currProject is not null, and it's projectId value matches the deleted id, set currProject to null:
		if(Objects.nonNull(currProject) && currProject.getProjectId().equals(deleteId)) {
			currProject=null;
		}
	}
	
	private boolean exitMenu() {
		System.out.println("Exiting menu.");
		return true;
	}
	
	private BigDecimal getDecimalInput(String prompt) {
		// Obtain String input from user:
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			// If input is null or a blank String, return null:
			return null;
		}
		
		try {
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number");
		}
	}

	private Integer getIntInput(String prompt) {
		// Obtain String input from user:
		String input = getStringInput(prompt);
				
		if(Objects.isNull(input)) {
			// If input is null or a blank String, return null:
			return null;
		}
		
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		// Get input from user:
		String input = scanner.nextLine();
		// If input is blank or consists only of spaces, return null. Otherwise, return trimmed form of input:
		return input.isBlank() ? null : input.trim();
	}
	
	
	
}
