package org.gjmorris;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class IssueTracker {

	private static final LocalTime amTime = LocalTime.of(9, 0);
	private static final LocalTime pmTime = LocalTime.of(17, 0);

	public LocalDateTime calculateDueDate(LocalDateTime submissionDate , int turnaroundHours) throws WorkTimeException {
		validateSubmissionDate(submissionDate);

		LocalDateTime dueDate;

		int turnaroundDays = turnaroundHours / 8;
		int remainingHours = turnaroundHours % 8;

		if (afterWorkHours(submissionDate.plusHours(remainingHours).toLocalTime())) {
			int hoursDiff = submissionDate.plusHours(remainingHours).getHour() - pmTime.getHour();
			dueDate = LocalDateTime.of(
				submissionDate.plusDays(1).toLocalDate(), 
				LocalTime.of(
					9 + hoursDiff,
					submissionDate.getMinute()
				)
			);
		} else {
			dueDate = submissionDate.plusDays(turnaroundDays).plusHours(remainingHours);
		}

		return dueDate;
	}

	private void validateSubmissionDate(LocalDateTime date) throws WorkTimeException {
		if (date.getDayOfWeek().ordinal() >= DayOfWeek.SATURDAY.ordinal()) {
			throw new WorkTimeException("Date: " + date + " was not submitted on a working day.\nExpected Weekday, Recieved " + date.getDayOfWeek());
		}

		if (date.toLocalTime().isBefore(amTime)) {
			throw new WorkTimeException("Date: " + date + " was submitted outside work hours.\nExpected time between 9AM - 5 PM, recieved " + date.toLocalTime());
		}

		if (afterWorkHours(date.toLocalTime())) {
			throw new WorkTimeException("Date: " + date + " was submitted outside work hours.\nExpected time between 9AM - 5 PM, recieved " + date.toLocalTime());
		}
	}

	private boolean afterWorkHours(LocalTime time) {
		return time.isAfter(pmTime);
	}

	public class WorkTimeException extends Exception {
		public WorkTimeException(String errorMessage) {
			super(errorMessage);
		}
	}
}
