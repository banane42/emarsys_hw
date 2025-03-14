package org.gjmorris;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class IssueTrackerTest {

	private final IssueTracker issueTracker = new IssueTracker();

	@Test public void hwExampleTest() {
		// Tuesday March 11, 2025 at 2:12 pm
		LocalDateTime submissionDate = LocalDateTime.of(2025, Month.MARCH, 11, 14, 12);
		int turnaroundHours = 16;
		// Thursday March 13, 2025 at 2:12 pm
		LocalDateTime expectedDate = LocalDateTime.of(2025, Month.MARCH, 13, 14, 12);

		try {
			LocalDateTime dueDate = issueTracker.calculateDueDate(submissionDate, turnaroundHours);
			assertEquals(expectedDate, dueDate);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	// Validating correct submission tests
	@Test(expected=IssueTracker.WorkTimeException.class)
	public void catchSaturdaySumbissionDays() throws IssueTracker.WorkTimeException {
		LocalDateTime saturdaySubmission = LocalDateTime.of(2025, Month.MARCH, 8, 9, 0);
		issueTracker.calculateDueDate(saturdaySubmission, 8);
	}

	@Test(expected=IssueTracker.WorkTimeException.class)
	public void catchSundaySumbissionDays() throws IssueTracker.WorkTimeException {
		LocalDateTime sundaySubmission = LocalDateTime.of(2025, Month.MARCH, 9, 9, 0);
		issueTracker.calculateDueDate(sundaySubmission, 8);
	}

	@Test public void validWorkdaySubmission() {
		try {
			for (int i = DayOfWeek.MONDAY.ordinal(); i < DayOfWeek.SATURDAY.ordinal(); i++) {
				LocalDateTime submissionDate = LocalDateTime.of(
					2025, 
					Month.MARCH, 
					3 + i, 
					9, 
					0
				);
				issueTracker.calculateDueDate(submissionDate, 8);
			}
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	@Test public void catchInvalidAMSubmissionTime() throws IssueTracker.WorkTimeException {
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 4, 8, 59);
		try {
			issueTracker.calculateDueDate(submission, 8);
		} catch (IssueTracker.WorkTimeException e) {
			return;
		}

		fail("Should have thrown WorkTime Exceptions");
	}

	@Test public void catchInvalidPMSubmissionTime() {
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 4, 17, 1);
		try {
			issueTracker.calculateDueDate(submission, 8);
		} catch (IssueTracker.WorkTimeException e) {
			System.out.println(e.toString());
			return;
		}

		fail("Should have thrown WorkTime Exceptions");
	}

	@Test public void checkValidSubmissionTimes() {
		LocalDateTime amSubmission = LocalDateTime.of(2025, Month.MARCH, 4, 9, 0);
		try {
			issueTracker.calculateDueDate(amSubmission, 8);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}

		LocalDateTime noonSubmission = LocalDateTime.of(2025, Month.MARCH, 4, 12, 0);
		try {
			issueTracker.calculateDueDate(noonSubmission, 8);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}

		LocalDateTime pmSubmission = LocalDateTime.of(2025, Month.MARCH, 4, 17, 0);
		try {
			issueTracker.calculateDueDate(pmSubmission, 8);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	// Correct Output Tests
	@Test public void sameDayDueDate() {
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 12, 10, 15);
		int turnaround = 4; // 0 days 4 hours
		LocalDateTime expectedDueDate = LocalDateTime.of(2025, Month.MARCH, 12, 14, 15);

		try {
			LocalDateTime dueDate = issueTracker.calculateDueDate(submission, turnaround);
			assertEquals(expectedDueDate, dueDate);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	@Test public void nextDayDueDate() {
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 12, 10, 0);
		int turnaround = 8; // 1 day 0 hours
		LocalDateTime expectedDueDate = LocalDateTime.of(2025, Month.MARCH, 13, 10, 0);

		try {
			LocalDateTime dueDate = issueTracker.calculateDueDate(submission, turnaround);
			assertEquals(expectedDueDate, dueDate);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	@Test public void nextDayAndSomeDueDate() {
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 12, 10, 0);
		int turnaround = 10; // 1 day 2 hours
		LocalDateTime expectedDueDate = LocalDateTime.of(2025, Month.MARCH, 13, 12, 0);

		try {
			LocalDateTime dueDate = issueTracker.calculateDueDate(submission, turnaround);
			assertEquals(expectedDueDate, dueDate);
			assert(dueDate.compareTo(expectedDueDate) == 0);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	@Test public void nextDayTickOverDueDate() {
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 12, 16, 0);
		int turnaround = 2; // 0 days 2 hours
		LocalDateTime expectedDueDate = LocalDateTime.of(2025, Month.MARCH, 13, 10, 0);

		try {
			LocalDateTime dueDate = issueTracker.calculateDueDate(submission, turnaround);
			assertEquals(expectedDueDate, dueDate);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	@Test public void nextDayMinuteTickOverDueDate() {
		// Submitted Monday MAR 3 at 4:45 pm
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 3, 16, 45);
		int turnaround = 2; // 0 days 2 hours
		// Expected on Tuesday MAR 4 at 10:45 am
		LocalDateTime expectedDueDate = LocalDateTime.of(2025, Month.MARCH, 4, 10, 45);

		try {
			LocalDateTime dueDate = issueTracker.calculateDueDate(submission, turnaround);
			assertEquals(expectedDueDate, dueDate);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	@Test public void nextWeekTickOverDueDate() {
		// Submitted Friday MAR 7 at 4pm
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 7, 16, 0);
		int turnaround = 4; // 0 days 4 hours
		// Expected on Monday MAR 10 at 12 pm
		LocalDateTime expectedDueDate = LocalDateTime.of(2025, Month.MARCH, 10, 12, 0);

		try {
			LocalDateTime dueDate = issueTracker.calculateDueDate(submission, turnaround);
			assertEquals(expectedDueDate, dueDate);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	@Test public void nextWeekBigTickOverDueDate() {
		// Submitted Wednesday MAR 12 at 12pm
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 12, 12, 0);
		int turnaround = 24; // 3 days 0 hours
		// Expected on Mon MAR 17 at 12 pm
		LocalDateTime expectedDueDate = LocalDateTime.of(2025, Month.MARCH, 17, 12, 0);

		try {
			LocalDateTime dueDate = issueTracker.calculateDueDate(submission, turnaround);
			assertEquals(expectedDueDate, dueDate);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	@Test public void nextWeekBiiigTickOverDueDate() {
		// Submitted Wednesday MAR 12 at 12pm
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 12, 12, 0);
		int turnaround = 72; // 9 days 0 hours
		// Expected on Mon MAR 25 at 12 pm
		LocalDateTime expectedDueDate = LocalDateTime.of(2025, Month.MARCH, 25, 12, 0);

		try {
			LocalDateTime dueDate = issueTracker.calculateDueDate(submission, turnaround);
			assertEquals(expectedDueDate, dueDate);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}

	@Test public void nextWeekBigTickOverAndSomeDueDate() {
		// Submitted Wednesday MAR 12 at 12pm
		LocalDateTime submission = LocalDateTime.of(2025, Month.MARCH, 12, 12, 0);
		int turnaround = 30; // 3 days 6 hours
		// Expected on Tuesday MAR 18 at 10 am
		LocalDateTime expectedDueDate = LocalDateTime.of(2025, Month.MARCH, 18, 10, 0);

		try {
			LocalDateTime dueDate = issueTracker.calculateDueDate(submission, turnaround);
			assertEquals(expectedDueDate, dueDate);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}
}