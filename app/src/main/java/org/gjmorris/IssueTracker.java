package org.gjmorris;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class IssueTracker {

	private static final LocalTime amTime = LocalTime.of(9, 0);
	private static final LocalTime pmTime = LocalTime.of(17, 0);

	public LocalDateTime calculateDueDate(LocalDateTime submissionDate , int turnaroundHours) throws WorkTimeException {
		validateSubmissionDate(submissionDate);

		LocalDateTime dueDate = LocalDateTime.from(submissionDate);

		int turnaroundDays = turnaroundHours / 8;
		int turnaroundWeeks = turnaroundDays / 5;
		int remainingTurnaroundDays = turnaroundDays % 5;
		int remainingHours = turnaroundHours % 8;

		// Check if the hours will put us on an invalid day
		if (afterWorkHours(dueDate.plusHours(remainingHours).toLocalTime())) {
			int hoursDiff = dueDate.plusHours(remainingHours).getHour() - pmTime.getHour();
			dueDate = LocalDateTime.of(
				submissionDate.plusDays(1).toLocalDate(), 
				LocalTime.of(
					9 + hoursDiff,
					dueDate.getMinute()
				)
			);
			remainingHours = 0;
		}

		dueDate = dueDate.plusHours(remainingHours);

		if (invalidWeekday(dueDate.toLocalDate())) {
			// if we landed on the weekend then we want to start on monday. So we have to advance
			// the day by 2 if Saturday and 1 if Sunday.
			// Enum DayOfWeek SATURDAY and SUNDAY have numeric values of 6 and 7 respectivly
			// So subtracting those values from 8 will get the number of days to advance.
			int days_advanced = 8 - dueDate.getDayOfWeek().getValue();
			dueDate = dueDate.plusDays(days_advanced);
		}

		dueDate = dueDate.plusWeeks(turnaroundWeeks);

		int days_remaining_in_week = DayOfWeek.FRIDAY.getValue() - dueDate.getDayOfWeek().getValue();
		dueDate = dueDate.plusDays(days_remaining_in_week);
		remainingTurnaroundDays -= days_remaining_in_week;

		if (invalidWeekday(dueDate.plusDays(remainingTurnaroundDays).toLocalDate())) {
			dueDate = dueDate.plusDays(2);
		}

		dueDate = dueDate.plusDays(remainingTurnaroundDays);

		return dueDate;
	}

	private void validateSubmissionDate(LocalDateTime date) throws WorkTimeException {
		if (invalidWeekday(date.toLocalDate())) {
			throw new WorkTimeException("Date: " + date + " was not submitted on a working day.\nExpected Weekday, Recieved " + date.getDayOfWeek());
		}

		if (date.toLocalTime().isBefore(amTime)) {
			throw new WorkTimeException("Date: " + date + " was submitted outside work hours.\nExpected time between 9AM - 5 PM, recieved " + date.toLocalTime());
		}

		if (afterWorkHours(date.toLocalTime())) {
			throw new WorkTimeException("Date: " + date + " was submitted outside work hours.\nExpected time between 9AM - 5 PM, recieved " + date.toLocalTime());
		}
	}

	private boolean invalidWeekday(LocalDate date) {
		return date.getDayOfWeek().ordinal() >= DayOfWeek.SATURDAY.ordinal();
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
