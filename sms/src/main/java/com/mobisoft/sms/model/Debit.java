package com.mobisoft.sms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "debit")
public class Debit {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;
	
	@OneToOne
	@JoinColumn(name = "u_id")
	private User userId;
	
	@Column(name = "debit")
	private int debit;
	
	@Column(name = "current_amount")
	private int currentAmount;
	
	@Column(name = "previous_amount")
	private int previousAmouunt;
	
	@Column(name = "debit_by")
	private String debitBy;
	
	@Column(name = "created", columnDefinition="DATETIME", nullable=true)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="IST")
	private Date created = new Date();
	
	@Column(name = "updated", columnDefinition="TIMESTAMP", nullable=true)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="IST")
	private Date updated;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public int getDebit() {
		return debit;
	}

	public void setDebit(int debit) {
		this.debit = debit;
	}

	public int getCurrentAmount() {
		return currentAmount;
	}

	public void setCurrentAmount(int currentAmount) {
		this.currentAmount = currentAmount;
	}

	public int getPreviousAmouunt() {
		return previousAmouunt;
	}

	public void setPreviousAmouunt(int previousAmouunt) {
		this.previousAmouunt = previousAmouunt;
	}

	public String getDebitBy() {
		return debitBy;
	}

	public void setDebitBy(String debitBy) {
		this.debitBy = debitBy;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
