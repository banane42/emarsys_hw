package org.gjmorris;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class IssueTracker {
	public LocalDateTime calculateDueDate(LocalDateTime submissionDate , int turnaroundHours) throws WorkTimeException {
		validateSubmissionDate(submissionDate);
		return null;
	}

	private void validateSubmissionDate(LocalDateTime date) throws WorkTimeException {
		if (date.getDayOfWeek().ordinal() >= DayOfWeek.SATURDAY.ordinal()) {
			throw new WorkTimeException("Date: " + date + " was not submitted on a working day.\nExpected Weekday, Recieved " + date.getDayOfWeek());
		}
	}

	public class WorkTimeException extends Exception {
		public WorkTimeException(String errorMessage) {
			super(errorMessage);
		}
	}
}
