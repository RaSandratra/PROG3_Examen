import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    public Team findTeamById(Integer id) {
        Team team = null;

        String teamQuery = "SELECT id, name FROM team WHERE id = ?";
        String playerQuery = "SELECT id, name, age, team_id, goal_nb FROM player WHERE team_id = ?";

        try (Connection connection = DBConnection.getDBConnection();
             PreparedStatement teamStmt = connection.prepareStatement(teamQuery);
             PreparedStatement playerStmt = connection.prepareStatement(playerQuery)) {

            teamStmt.setInt(1, id);
            ResultSet teamRs = teamStmt.executeQuery();

            if (teamRs.next()) {
                team = new Team();
                team.setId(teamRs.getInt("id"));
                team.setName(teamRs.getString("name"));
            } else {
                return null;
            }

            playerStmt.setInt(1, id);
            ResultSet playerRs = playerStmt.executeQuery();

            List<Player> players = new ArrayList<>();
            while (playerRs.next()) {
                Player player = new Player();
                player.setId(playerRs.getInt("id"));
                player.setName(playerRs.getString("name"));
                player.setAge(playerRs.getInt("age"));
                player.setTeamId(playerRs.getInt("team_id"));

                int goalNb = playerRs.getInt("goal_nb");
                if (playerRs.wasNull()) {
                    player.setGoalNb(null);
                } else {
                    player.setGoalNb(goalNb);
                }

                players.add(player);
            }

            team.setPlayers(players);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return team;
    }

    public List<Player> findPlayers(int page, int size) {
        List<Player> players = new ArrayList<>();

        int offset = (page - 1) * size;

        String query = "SELECT id, name, age, team_id, goal_nb FROM player ORDER BY id LIMIT ? OFFSET ?";

        try (Connection connection = DBConnection.getDBConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setName(rs.getString("name"));
                player.setAge(rs.getInt("age"));
                player.setTeamId(rs.getInt("team_id"));

                int goalNb = rs.getInt("goal_nb");
                if (rs.wasNull()) {
                    player.setGoalNb(null);
                } else {
                    player.setGoalNb(goalNb);
                }

                players.add(player);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }

    public List<Player> createPlayers(List<Player> newPlayers) {
        List<Player> createdPlayers = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DBConnection.getDBConnection();
            connection.setAutoCommit(false);

            String checkQuery = "SELECT COUNT(*) FROM player WHERE name = ?";
            String insertQuery = "INSERT INTO player (name, age, team_id, goal_nb) VALUES (?, ?, ?, ?)";

            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

            for (Player player : newPlayers) {
                checkStmt.setString(1, player.getName());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RuntimeException("Le joueur '" + player.getName() + "' existe déjà");
                }
            }

            for (Player player : newPlayers) {
                insertStmt.setString(1, player.getName());
                insertStmt.setInt(2, player.getAge());

                if (player.getTeamId() != null) {
                    insertStmt.setInt(3, player.getTeamId());
                } else {
                    insertStmt.setNull(3, Types.INTEGER);
                }

                if (player.getGoalNb() != null) {
                    insertStmt.setInt(4, player.getGoalNb());
                } else {
                    insertStmt.setNull(4, Types.INTEGER);
                }

                insertStmt.executeUpdate();

                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    player.setId(generatedKeys.getInt(1));
                    createdPlayers.add(player);
                }
            }

            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Erreur lors de la création des joueurs: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return createdPlayers;
    }

    public Team saveTeam(Team teamToSave) {
        Connection connection = null;

        try {
            connection = DBConnection.getDBConnection();

            String checkQuery = "SELECT id FROM team WHERE id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, teamToSave.getId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String updateQuery = "UPDATE team SET name = ? WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setString(1, teamToSave.getName());
                updateStmt.setInt(2, teamToSave.getId());
                updateStmt.executeUpdate();
            } else {
                String insertQuery = "INSERT INTO team (name) VALUES (?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                insertStmt.setString(1, teamToSave.getName());
                insertStmt.executeUpdate();

                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    teamToSave.setId(generatedKeys.getInt(1));
                }
            }

            return teamToSave;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la sauvegarde de l'équipe", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updatePlayerGoals(Integer playerId, Integer goalNb) {
        String query = "UPDATE player SET goal_nb = ? WHERE id = ?";

        try (Connection connection = DBConnection.getDBConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            if (goalNb != null) {
                stmt.setInt(1, goalNb);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setInt(2, playerId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour des buts du joueur", e);
        }
    }

    public Player findPlayerById(Integer id) {
        Player player = null;
        String query = "SELECT id, name, age, team_id, goal_nb FROM player WHERE id = ?";

        try (Connection connection = DBConnection.getDBConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                player = new Player();
                player.setId(rs.getInt("id"));
                player.setName(rs.getString("name"));
                player.setAge(rs.getInt("age"));
                player.setTeamId(rs.getInt("team_id"));

                int goalNb = rs.getInt("goal_nb");
                if (rs.wasNull()) {
                    player.setGoalNb(null);
                } else {
                    player.setGoalNb(goalNb);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return player;
    }
}
