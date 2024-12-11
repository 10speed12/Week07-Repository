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
			, "3) Select a project");
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
