package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.entity.Category;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDAO extends DaoBase{
	// Create and define table name constants:
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
	public Project insertProject(Project project) {
		// @formatter:off
		String sql = ""
		+"INSERT INTO " + PROJECT_TABLE + " " 
		+"(project_name, estimated_hours, actual_hours, difficulty, notes) "
		+ "VALUES "
		+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				return project;
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch(SQLException e) {
			throw new DbException(e);
		}
	}

	public List<Project> fetchAllProjects() {
		// Set up SELECT statement to obtain all rows from the PROJECT_TABLE sorted by project_name:
		// @formatter:off
		String sql = ""
		+ "SELECT * " 		
		+ "FROM " + PROJECT_TABLE + " " 
		+ "ORDER BY project_name";
		// @formatter:on
		// Attempt to connect to database:
		try(Connection conn = DbConnection.getConnection()){
			// Begin transaction on database if connection was successful:
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				// Obtain ResultSet of data obtained by executing the prepared query:
				try(ResultSet rs = stmt.executeQuery()){
					List<Project> projects = new LinkedList<>();
					// While there are still entries in rs:
					while(rs.next()) {
				// Add the contents of the current row in rs to projects after converting the data into a Project item:
						projects.add(extract(rs,Project.class));
					}
					// Return generated list of projects in the database:
					return projects;
				}
				
			}
			catch(Exception e) {
				// Roll back transaction if an error occurred:
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch(SQLException e) {
			throw new DbException(e);
		}
	}

	public Optional<Project> fetchProjectById(Integer projectId) {
		// Set up SELECT statement to obtain row in PROJECT_TABLE with matching project_id:
		// @formatter:off
		String sql = ""
		+ "SELECT * " 		
		+ "FROM " + PROJECT_TABLE + " " 
		+ "WHERE project_id = ?";
		// @formatter:on
		// Attempt to connect to the database:
		try(Connection conn = DbConnection.getConnection()){
			// Begin transaction on database if connection was successful:
			startTransaction(conn);
			try{
				Project project = null;
				// Prepare to execute the SQL statement:
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					// Set placeholder ? in the statement to the value of projectId:
					setParameter(stmt, 1, projectId, Integer.class);
					// Attempt to obtain result set of executing the SQL query:
					try(ResultSet rs = stmt.executeQuery()){
						if(rs.next()) {
							// If rs has a row in it, extract the row and store its contents in project:
							project = extract(rs,Project.class);
						}
					}
				}
				// Confirm that project is not set to null:
				if(Objects.nonNull(project)) {
					// If project is not null, obtain the project's list categories, materials, and steps:
					project.getMaterials().addAll(fetchMaterialsForProject(conn,projectId));
					project.getSteps().addAll(fetchStepsForProject(conn,projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn,projectId));
				}
				
				// commit results of transaction:
				commitTransaction(conn);
				return Optional.ofNullable(project);
			}
			catch(Exception e) {
				// Roll back transaction if an error occurred:
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch(SQLException e) {
			throw new DbException(e);
		}
	}

	
	// Obtain rows from MATERIAL_TABLE with designated projectId:
	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException{
		// Set up SELECT statement to obtain row in MATERIAL_TABLE with matching project_id:
		// @formatter:off
		String sql = ""
		+ "SELECT *" 		
		+ "FROM " + MATERIAL_TABLE + " " 
		+ "WHERE project_id = ?";
		// @formatter:on
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt,1,projectId,Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Material> materials = new LinkedList<>();
				while(rs.next()) {
					materials.add(extract(rs,Material.class));
				}
				return materials;
			}
		}
	}
	
	// Obtain rows from STEP_TABLE with designated projectId:
	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException{
		// Set up SELECT statement to obtain row in STEP_TABLE with matching project_id:
		// @formatter:off
		String sql = ""
		+ "SELECT *" 		
		+ "FROM " + STEP_TABLE + " " 
		+ "WHERE project_id = ?";
		// @formatter:on
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt,1,projectId,Integer.class);
					
			try(ResultSet rs = stmt.executeQuery()){
				List<Step> steps = new LinkedList<>();
				while(rs.next()) {
					steps.add(extract(rs,Step.class));
				}
				return steps;
			}
		}
	}
	// Obtain rows from CATEGORY_TABLE with designated projectId:
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException{
		// Set up SELECT statement to obtain row in CATEGORY_TABLE with matching project_id:
		// @formatter:off
		String sql = ""
		+ "SELECT c.*" 		
		+ "FROM " + CATEGORY_TABLE + " c " 
		+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id)"
		+ "WHERE project_id = ?";
		// @formatter:on
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt,1,projectId,Integer.class);
						
			try(ResultSet rs = stmt.executeQuery()){
				List<Category> categories = new LinkedList<>();
				while(rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			}
		}
	}

	public boolean modifyProjectDetails(Project project) {
		// @formatter:off
		String sql = ""
				+ "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?, "
				+ "actual_hours = ?, "
				+ "difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ?";
		// @formatter:on
		System.out.println(sql);
		try(Connection conn = DbConnection.getConnection()){
			// Start the new transaction:
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				// Set the first ? placeholder in the SQL statement to projectName from project:
				setParameter(stmt, 1, project.getProjectName(), String.class);
				// Set the second ? placeholder in the SQL statement to estimatedHours from project:
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				// Set the third ? placeholder in the SQL statement to actualHours from project:
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				// Set the fourth ? placeholder in the SQL statement to difficulty from project:
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				// Set the fifth ? placeholder in the SQL statement to notes from project:
				setParameter(stmt, 5, project.getNotes(), String.class);
				// Set the sixth ? placeholder in the SQL statement to projectId from project:
				setParameter(stmt, 6, project.getProjectId(), Integer.class);
				System.out.println(project);
				// Confirm results of update:
				boolean modified = stmt.executeUpdate(sql) == 1;
				// Commit results of update to the database:
				commitTransaction(conn);
				// Return whether or not the update was successful:
				return modified;
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch (SQLException e) {
			throw new DbException(e);
		}
	}
}
