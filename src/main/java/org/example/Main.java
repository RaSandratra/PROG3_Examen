import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbconnection = new DBConnection();

    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();
//        Team realMadrid = dataRetriever.findTeamById(1);
//        System.out.println(realMadrid);

        List<Player> players = dataRetriever.findPlayers(3, 2);
        System.out.println(players);

        List<Player> players_list = List.of(
                new Player(
                    1,
                        "Messi",
                        31,
                        PlayerPositionEnum.MIDF
                )
        );

        dataRetriever.createPlayers(players_list);
        System.out.println(players_list);
    }

    public Team findTeamById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        String sql = "SELECT id, name, continent FROM team WHERE id = ?";
        Connection databaseConnection = dbconnection.getDBConnection();
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new RuntimeException("team not found");
            }

            Integer id_team = resultSet.getInt("id");
            String name = resultSet.getString("name");
            ContinentEnum continent = ContinentEnum.valueOf(resultSet.getString("continent"));
            List<Player> players = findPlayersInTeam(id);

            Team teamFromDatabase = new Team(id_team, name, continent, players);
            teamFromDatabase.setPlayers();

            return teamFromDatabase;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            dbconnection.closeDbConnection();
        }
    }

    public List<Player> findPlayers(int page, int size) {
        if (page < 0 || size < 0) {
            throw new IllegalArgumentException("page or size cannot be negative");
        }

        String sql =
                """
                        SELECT id, name, age, position, id_team 
                        FROM player 
                        order by id LIMIT ? OFFSET ? """;
        Connection databaseConnection = dbconnection.getDBConnection();
        int offset = (page - 1) * size;
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setInt(1, size);
            preparedStatement.setInt(2, offset);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Player> playersFromDatabase = new ArrayList<>();

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                PlayerPositionEnum position = PlayerPositionEnum.valueOf(resultSet.getString("position"));
                Integer id_team = resultSet.getInt("id_team");

                Player player = new Player(id, name, age, position);

                if (id_team != null) {
                    player.setTeam(findTeamById(id_team));
                }

                playersFromDatabase.add(player);
            }

            return playersFromDatabase;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            dbconnection.closeDbConnection();
        }
    }

    public List<Player> createPlayers(List<Player> newPlayers) {
        if (newPlayers.isEmpty()) {
            return new ArrayList<>();
        }
        String sql =
                """
                        insert into player (id, name, age, position, id_team)
                        values (?, ?, ?, ?, ?)""";
        Connection databaseConnection = dbconnection.getDBConnection();
        try {
            databaseConnection.setAutoCommit(false);
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql);

            for (Player newPlayer : newPlayers) {
                Integer id = newPlayer.getId();
                String name = newPlayer.getName();
                int age = newPlayer.getAge();
                PlayerPositionEnum position = newPlayer.getPosition();

                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setInt(3, age);
                preparedStatement.setString(4, position.toString());

                if (newPlayer.getTeam() != null) {
                    Integer id_team = newPlayer.getTeam().getId();
                    preparedStatement.setInt(5, id_team);
                } else {
                    preparedStatement.setNull(5, Types.INTEGER);
                }

                preparedStatement.executeUpdate();
            }

            databaseConnection.commit();
            databaseConnection.setAutoCommit(true);
            return newPlayers;

        } catch (Exception e) {
            try {
                databaseConnection.rollback();
                throw new RuntimeException(e);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            dbconnection.closeDbConnection();
        }
    }

    public Team saveTeam(Team teamToSave) {
        return null;
    }

    public List<Team> findTeamsByPlayerName(String playerName) {
        return null;
    }

    public List<Player> findPlayersByCriteria(String playerName, PlayerPositionEnum position, String teaName, ContinentEnum continent, int page, int size) {
        return null;
    }

    private List<Player> findPlayersInTeam(Integer id) {
        String sql = "SELECT id, name, age, position from player WHERE id_team = ?";
        Connection databaseConnection = dbconnection.getDBConnection();

        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Player> playersFromDatabase = new ArrayList<>();

            while (resultSet.next()) {
                Integer id_player = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                PlayerPositionEnum position = PlayerPositionEnum.valueOf(resultSet.getString("position"));

                Player player = new Player(id_player, name, age, position);
                playersFromDatabase.add(player);
            }

            return playersFromDatabase;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            dbconnection.closeDbConnection();
        }
    }
}
