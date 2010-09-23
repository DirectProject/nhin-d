package org.nhindirect.config.store.dao.impl;

import java.util.List;

import org.nhindirect.config.store.Anchor;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.AnchorDao;

public class AnchorDaoImpl implements AnchorDao {

	public Anchor load(String owner) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Anchor> listAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Anchor> list(List<String> owners) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(Anchor anchor) {
		// TODO Auto-generated method stub

	}

	public void save(List<Anchor> anchorList) {
		// TODO Auto-generated method stub

	}

	public void setStatus(List<Long> anchorIDs, EntityStatus status) {
		// TODO Auto-generated method stub

	}

	public void setStatus(String owner, EntityStatus status) {
		// TODO Auto-generated method stub

	}

	public void delete(List<Long> idList) {
		// TODO Auto-generated method stub

	}

	public void delete(String owner) {
		// TODO Auto-generated method stub

	}

}
