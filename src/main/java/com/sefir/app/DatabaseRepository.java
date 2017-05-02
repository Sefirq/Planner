package com.sefir.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;

@Repository
public class DatabaseRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public List<Meeting> findAll() {
        return jdbcTemplate.query("SELECT * FROM meetings", new MeetingRowMapper());
    }

    @Transactional(readOnly = true)
    public List<Meeting> findAllWithMeetingRoomID(int meetingRoomID) {
        return jdbcTemplate.query("SELECT * FROM meetings WHERE meetingRoomID=" + Integer.toString(meetingRoomID), new MeetingRowMapper());
    }

    /**
     * @param meeting - meeting object to be inserted into database
     * @return meeting - same object but with automatically generated (H2 database generates it) new meeting's ID
     */
    public Meeting create(final Meeting meeting) {
        final String sqlInsertMeeting = "INSERT INTO meetings VALUES(default, ?, ?, ?, ?, ?, ?)";

        KeyHolder holder = new GeneratedKeyHolder(); // to test if INSERT worked
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertMeeting, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, meeting.getName());
            preparedStatement.setString(2, meeting.getDescription());
            preparedStatement.setInt(3, meeting.getDuration());
            preparedStatement.setString(4, meeting.getDate());
            preparedStatement.setString(5, meeting.getTime());
            preparedStatement.setInt(6, meeting.getMeetingRoomID());
            return preparedStatement;
        }, holder);

        int newUserID = holder.getKey().intValue();
        meeting.setId(newUserID);
        return meeting;
    }

    public void deleteByID(int meetingID) {
        final String sqlDeleteMeeting = "DELETE FROM meetings WHERE id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteMeeting);
            preparedStatement.setInt(1, meetingID);
            return preparedStatement;
        });
    }
}

/**
 * This class maps JDBC's ResultSet to proper Meeting objects. One row creates one object with all the fields filled.
 */
class MeetingRowMapper implements RowMapper<Meeting> {
    @Override
    public Meeting mapRow(ResultSet rs, int rowNum) throws SQLException {
        Meeting meeting = new Meeting();
        meeting.setId(rs.getInt("id"));
        meeting.setName(rs.getString("name"));
        meeting.setDescription(rs.getString("description"));
        meeting.setDuration(rs.getInt("duration"));
        meeting.setDate(rs.getString("date"));
        meeting.setTime(rs.getString("time"));
        meeting.setMeetingRoomID(rs.getInt("meetingRoomID"));
        return meeting;
    }
}