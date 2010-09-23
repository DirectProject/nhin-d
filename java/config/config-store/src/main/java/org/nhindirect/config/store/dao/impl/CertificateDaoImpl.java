package org.nhindirect.config.store.dao.impl;

import java.util.List;

import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.CertificateDao;

public class CertificateDaoImpl implements CertificateDao {

	public Certificate load(String owner, String thumbprint) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Certificate> list(List<Long> idList) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Certificate> list(String owner) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(Certificate cert) {
		// TODO Auto-generated method stub

	}

	public void save(List<Certificate> certList) {
		// TODO Auto-generated method stub

	}

	public void setStatus(List<Long> certificateIDs, EntityStatus status) {
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
