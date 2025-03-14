package org.gjmorris;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class IssueTracker {

	private static final LocalTime amTime = LocalTime.of(9, 0);
	private static final LocalTime pmTime = LocalTime.of(17, 0);

	public LocalDateTime calculateDueDate(LocalDateTime submissionDate , int turnaroundHours) throws WorkTimeException {
		validateSubmissionDate(submissionDate);
		return null;
	}

	private void validateSubmissionDate(LocalDateTime date) throws WorkTimeException {
		if (date.getDayOfWeek().ordinal() >= DayOfWeek.SATURDAY.ordinal()) {
			throw new WorkTimeException("Date: " + date + " was not submitted on a working day.\nExpected Weekday, Recieved " + date.getDayOfWeek());
		}

		if (date.toLocalTime().isBefore(amTime)) {
			System.out.println("Before AM");
			throw new WorkTimeException("Date: " + date + " was submitted outside work hours.\nExpected time between 9AM - 5 PM, recieved " + date.toLocalTime());
		}
		if (date.toLocalTime().isAfter(pmTime)) {
			System.out.println("After PM");
			throw new WorkTimeException("Date: " + date + " was submitted outside work hours.\nExpected time between 9AM - 5 PM, recieved " + date.toLocalTime());
		}
	}

	public class WorkTimeException extends Exception {
		public WorkTimeException(String errorMessage) {
			super(errorMessage);
		}
	}
}
