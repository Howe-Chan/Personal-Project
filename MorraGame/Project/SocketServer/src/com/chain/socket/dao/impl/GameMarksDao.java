package com.chain.socket.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chain.socket.dao.IGameMarksDao;
import com.chain.socket.dbUtil.DBUtils;
import com.chain.socket.model.GameMarks;

public class GameMarksDao implements IGameMarksDao {

	// JDBC
	private DBUtils dbUtils = new DBUtils();

	@Override
	public long add(GameMarks bean) {
		// TODO Auto-generated method stub
		String sql = "insert into game_marks(userName,grade) values(?,0)";
		Object[] params = new Object[] { bean.getUserName() };
		return dbUtils.executeUpdate(sql, params);
	}

	@Override
	public void delete(long id) {
		// TODO Auto-generated method stub
		String sql = "delete from game_marks where id=?";
		Object[] params = new Object[] { id };
		dbUtils.executeUpdate(sql, params);
	}

	@Override
	public long update(GameMarks bean) {
		// TODO Auto-generated method stub
		String sql = "update game_marks set grade=? where userName=?";
		Object[] params = new Object[] { bean.getGrade(), bean.getUserName() };
		return dbUtils.executeUpdate(sql, params);
	}

	@Override
	public GameMarks findById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GameMarks> findAll() {
		// TODO Auto-generated method stub
		String sql = "select * from game_marks order by grade desc limit 0,10";
		List<Object> list = dbUtils.excuteQuery(sql, null);
		List<GameMarks> glist = new ArrayList<GameMarks>();
		for (Object o : list) {
			Map<String,Object> map = (Map<String, Object>) o;
			GameMarks gameMarks = new GameMarks();
			gameMarks.setUserName(String.valueOf(map.get("userName")));
			gameMarks.setGrade(Long.parseLong(String.valueOf(map.get("grade"))));
			glist.add(gameMarks);
		}
		return glist;
	}

	@Override
	public GameMarks findByExample(String property, Object value) {
		// TODO Auto-generated method stub
		String sql = "select * from game_marks where " + property + "=?";
		Object[] params = new Object[] { value };
		Map<String,Object> map = dbUtils.executeQuery(sql, params);
		GameMarks gameMarks = null;
		if(map != null){
			gameMarks= new GameMarks();
			gameMarks.setUserName(String.valueOf(map.get("userName")));
			gameMarks.setGrade(Long.parseLong(String.valueOf(map.get("grade"))));
		}
		return gameMarks;
	}

}
