package com.mobisoft.sms.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.mobisoft.sms.model.SmsBalance;

public interface SmsHelperService {

	public List<Integer> getBalance(int userId,int productId);
	public int deductBalanceDeleteUser(int userId,int resellerId,String remark,int deductType,Session session,Transaction tx);
	public int updateUserBalance(int newBalance,int userId,int productId,Session session,Transaction tx);
	public int creditBalance(int userId,int productId,int resellerId,int balance,String remark,int creditType,Session session,Transaction tx);
	public int debitBalnce(int userId,int productId,int resellerId,int balance,String remark,int debitType,Session session,Transaction tx);
	public List<SmsBalance> findProdcut(int userId,int prodcutId);
}
