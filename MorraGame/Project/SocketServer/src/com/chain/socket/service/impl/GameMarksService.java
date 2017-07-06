package com.chain.socket.service.impl;

import java.util.List;

import com.chain.socket.dao.IGameMarksDao;
import com.chain.socket.dao.impl.GameMarksDao;
import com.chain.socket.model.GameMarks;
import com.chain.socket.service.IGameMarksService;

public class GameMarksService implements IGameMarksService {

	private IGameMarksDao gameMarksDao = new GameMarksDao();

	@Override
	public boolean add(GameMarks bean) {
		// TODO Auto-generated method stub
		boolean temp = false;
		long result = 0;
		if (gameMarksDao.findByExample("userName", bean.getUserName()) == null) {
			result = gameMarksDao.add(bean);
		}
		if (result > 0) {
			temp = true;
		}
		return temp;
	}

	@Override
	public boolean update(GameMarks bean) {
		// TODO Auto-generated method stub
		boolean temp = false;
		long result = 0;
		GameMarks mGameMarks = gameMarksDao.findByExample("userName", bean.getUserName());
		if (mGameMarks != null) {
			long grade = mGameMarks.getGrade() + bean.getGrade();
			mGameMarks.setGrade(grade);
			result = gameMarksDao.update(mGameMarks);
		}
		if (result > 0) {
			temp = true;
		}
		return temp;
	}

	@Override
	public List<GameMarks> findAll() {
		// TODO Auto-generated method stub
		return gameMarksDao.findAll();
	}

}
