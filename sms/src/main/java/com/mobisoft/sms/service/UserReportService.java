package com.mobisoft.sms.service;

import java.util.List;

import com.mobisoft.sms.model.DlrStatus;

public interface UserReportService {
	public List todayCountMessage(int userId);
	public List weeklyCountMessage(int userId);
	public List monthlyCountMessage(int userId);
}
