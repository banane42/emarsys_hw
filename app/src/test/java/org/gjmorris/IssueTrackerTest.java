package org.gjmorris;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.fail;
import org.junit.Test;

public class IssueTrackerTest {

	private final IssueTracker issueTracker = new IssueTracker();

	@Test public void hwExampleTest() {
		// Tuesday March 11, 2025 at 2:12 pm
		LocalDateTime submissionDate = LocalDateTime.of(2025, Month.MARCH, 11, 14, 12);
		int turnaroundHours = 16;

		try {
			LocalDateTime calculatedDate = issueTracker.calculateDueDate(submissionDate, turnaroundHours);
			// Thursday March 13, 2025 at 2:12 pm
			LocalDateTime expectedDate = LocalDateTime.of(2025, Month.MARCH, 13, 14, 12);
			assert(calculatedDate.compareTo(expectedDate) == 0);
		} catch (IssueTracker.WorkTimeException e) {
			fail(e.toString());
		}
	}
}