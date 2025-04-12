/**
   Copyright 2025 Vassili Dzuba

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.	
**/

package vassilidzuba.yacic.persistence;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.persistence.Jdbc.Mapper;

@Slf4j
public class PersistenceManager {
	private static final String COLUMN_PROJECT_ID = "project_id";
	private static final String BRANCH_ID = "branch_id";

	private static HikariConfig config = new HikariConfig();
	private static HikariDataSource ds;

	private static String databaseURL;
	private static String databaseUser;
	private static String databasePassword;
	private static boolean inited = false;

	private static Jdbc init() {
		if (!inited) {
			if (databaseURL == null) {
				setDatabaseConfig("sa");
			}

			config.setJdbcUrl(databaseURL);
			config.setUsername(databaseUser);
			config.setPassword(databasePassword);
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

			if (databaseURL.contains("postgres")) {
				config.setDriverClassName("org.postgresql.Driver");
			} else {
				config.setDriverClassName("org.h2.Driver");
			}

			ds = new HikariDataSource(config);
			inited = true;
		}
		return new Jdbc(ds);
	}

	public static void setDatabaseConfig(String password) {
		databaseURL = "jdbc:h2:mem:;INIT=RUNSCRIPT FROM 'yacic.sql';";
		databaseUser = "sa";
		databasePassword = password;
		init();
	}

	public static void setDatabaseConfig(String url, String user, String password) {
		databaseURL = url;
		databaseUser = user;
		databasePassword = password;
		init();
	}

	public void storeProject(String project, String repo, Map<String, String> branches) {
		var db = init();

		db.store("""
				insert into projects(project_id, repo)
				values(?,?);
				""", project, repo);

		for (var e : branches.entrySet()) {
			log.info("-- {} - {} - {}", project, e.getKey(), e.getValue());

			db.store("""
					insert into branches(project_id, branch_id, branchdir)
					values(?,?,?);
					""", project, e.getKey(), e.getValue());
		}
	}

	public void storeBranch(String project, String branch, String branchdir) {
		log.info("-- {} - {} - {}", project, branch, branchdir);

		init().store("""
				insert into branches(project_id, branch_id, branchdir)
				values(?,?,?);
				""", project, branch, branchdir);
	}

	public void storeBuild(String project, String branch, String timestamp, String status, int duration) {
		init().store("""
				insert into builds(project_id, branch_id, timestamp, status, duration)
				values(?, ?, ?, ?, ?);
				""", project, branch, timestamp, status, duration);
	}

	public void storeStep(String project, String branch, String timestamp, String step, int seq, String status, int duration) {
		init().store("""
				insert into steps(project_id, branch_id, timestamp, step_id, seq, status, duration)
				values(?, ?, ?, ?, ?, ?, ?);
				""", project, branch, timestamp, step, seq, status, duration);
	}

	public List<Project> listProjects() {
		return init().select("select project_id, repo from projects order by project_id", new ProjectMapper());
	}

	public Optional<Project> getProject(String id) {
		var projects = init().select("select project_id, repo from projects where project_id = ?", new ProjectMapper(), id);
		if (!projects.isEmpty()) {
			return Optional.of(projects.get(0));
		} else {
			return Optional.empty();
		}
	}

	public List<Branch> listBranches(String project) {
		return init().select("select project_id, branch_id, branchdir from branches where project_id = ?",
				new BranchMapper(), project);
	}

	public Optional<Branch> getBranch(String project, String branch) {
		var branches = init().select(
				"select project_id, branch_id, branchdir from branches where project_id=? and branch_id=?",
				new BranchMapper(), project, branch);
		if (!branches.isEmpty()) {
			return Optional.of(branches.get(0));
		} else {
			return Optional.empty();
		}
	}

	public List<Build> listBuilds(String project, String branch) {
		return init().select(
				"select project_id, branch_id, timestamp, status, duration from builds where project_id=? and branch_id=? order by timestamp desc",
				new BuildMapper(), project, branch);
	}

	public Optional<Build> getBuild(String project, String branch, String timestamp) {
		var builds = init().select(
				"select project_id, branch_id, timestamp, status, duration from builds where project_id=? and branch_id=? and timestamp=?",
				new BuildMapper(), project, branch, timestamp);
		if (!builds.isEmpty()) {
			return Optional.of(builds.get(0));
		} else {
			return Optional.empty();
		}
	}

	public List<Step> listSteps(String project, String branch, String timestamp) {
		return init().select(
				"select project_id, branch_id, timestamp, step_id, seq, status, duration from steps where project_id=? and branch_id=? and timestamp=? order by seq desc",
				new StepMapper(), project, branch, timestamp);
	}

	public Optional<Step> getStep(String project, String branch, String timestamp, String step) {
		var steps = init().select(
				"select project_id, branch_id, timestamp, step_id, seq, status, duration from steps where project_id=? and branch_id=? and timestamp=? and step_id=?",
				new StepMapper(), project, branch, timestamp, step);
		if (!steps.isEmpty()) {
			return Optional.of(steps.get(0));
		} else {
			return Optional.empty();
		}
	}

	public static class Project {
		@Setter
		@Getter
		private String projectId;
		@Setter
		@Getter
		private String repo;

		@Override
		public String toString() {
			return String.format("[project %s repo:%s]", projectId, repo);
		}
	}

	static class ProjectMapper implements Mapper<Project> {

		@SneakyThrows
		@Override
		public Project map(ResultSet rs) {
			var project = new Project();
			project.setProjectId(rs.getString(COLUMN_PROJECT_ID));
			project.setRepo(rs.getString("repo"));
			return project;
		}
	}

	public static class Branch {
		@Setter
		@Getter
		private String projectId;
		@Setter
		@Getter
		private String branchId;
		@Setter
		@Getter
		private String branchdir;

		@Override
		public String toString() {
			return String.format("[branch %s %s dir:%s]", projectId, branchId, branchdir);
		}
	}

	static class BranchMapper implements Mapper<Branch> {

		@SneakyThrows
		@Override
		public Branch map(ResultSet rs) {
			var branch = new Branch();
			branch.setProjectId(rs.getString(COLUMN_PROJECT_ID));
			branch.setBranchId(rs.getString(BRANCH_ID));
			branch.setBranchdir(rs.getString("branchdir"));
			return branch;
		}
	}

	public static class Build {
		@Setter
		@Getter
		private String projectId;
		@Setter
		@Getter
		private String branchId;
		@Setter
		@Getter
		private String timestamp;
		@Setter
		@Getter
		private int duration;
		@Setter
		@Getter
		private String status;

		@Override
		public String toString() {
			return String.format("[build %s %s %s status:%s]", projectId, branchId, timestamp, status);
		}
	}

	static class BuildMapper implements Mapper<Build> {

		@SneakyThrows
		@Override
		public Build map(ResultSet rs) {
			var build = new Build();
			build.setProjectId(rs.getString(COLUMN_PROJECT_ID));
			build.setBranchId(rs.getString(BRANCH_ID));
			build.setTimestamp(rs.getString("timestamp"));
			build.setStatus(rs.getString("status"));
			build.setDuration(rs.getInt("duration"));
			return build;
		}
	}

	public static class Step {
		@Setter
		@Getter
		private String projectId;
		@Setter
		@Getter
		private String branchId;
		@Setter
		@Getter
		private String timestamp;
		@Setter
		@Getter
		private String stepId;
		@Setter
		@Getter
		private int seq;
		@Setter
		@Getter
		private int duration;
		@Setter
		@Getter
		private String status;

		@Override
		public String toString() {
			return String.format("[step %s %s %s %s seq:%d duration:%d status:%s]", projectId, branchId, timestamp,
					stepId, seq, duration, status);
		}
	}

	static class StepMapper implements Mapper<Step> {

		@SneakyThrows
		@Override
		public Step map(ResultSet rs) {
			var step = new Step();
			step.setProjectId(rs.getString(COLUMN_PROJECT_ID));
			step.setBranchId(rs.getString(BRANCH_ID));
			step.setTimestamp(rs.getString("timestamp"));
			step.setStepId(rs.getString("step_id"));
			step.setSeq(rs.getInt("seq"));
			step.setDuration(rs.getInt("duration"));
			step.setStatus(rs.getString("status"));
			return step;
		}
	}
}