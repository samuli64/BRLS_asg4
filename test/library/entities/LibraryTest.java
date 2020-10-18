package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.helpers.IBookHelper;
import library.entities.helpers.ILoanHelper;
import library.entities.helpers.IPatronHelper;

@ExtendWith(MockitoExtension.class)
class LibraryTest {
	
	ILibrary library;
	@Mock IBookHelper bookHelper;
	@Mock IPatronHelper patronHelper;
	@Mock ILoanHelper loanHelper;
	Map<Integer, IBook> catalog = new HashMap<Integer, IBook>();
	Map<Integer, IPatron> patrons = new HashMap<Integer, IPatron>();
	Map<Integer, ILoan> loans = new HashMap<Integer, ILoan>();
	Map<Integer, ILoan> currentLoans = new HashMap<Integer, ILoan>();
	Map<Integer, IBook> damagedBooks = new HashMap<Integer, IBook>();

	IBook book;
	IPatron patron;
	ILoan loan;
	
	String author = "Herman Melville";
	String title = "Moby Dick";
	String callNo = "c123";
	int bookId = 1;
	
	String lastName = "Mustermann";
	String firstName = "Max";
	String email = "max.mustermann@example.com";
	long phoneNumber = 123456789;
	int patronId = 1;
	Map<Integer, ILoan> patronloans = new HashMap<Integer, ILoan>();
	
	int loanId = 1;
	
	@BeforeEach
	void setUp() throws Exception {
		library = new Library(bookHelper, patronHelper, loanHelper, catalog,
				patrons, loans, currentLoans, damagedBooks);

		book = new Book(author, title, callNo, bookId, IBook.BookState.ON_LOAN);
		patron = new Patron(lastName, firstName, email, phoneNumber, patronId, 0.0,
				IPatron.PatronState.CAN_BORROW, patronloans);

		patrons.put(patron.getId(), patron);
		catalog.put(book.getId(), book);
		
		Calendar.getInstance().setDate(new Date());
	}

	@Test
	void calculateOverDueFine_WhenLoanOverdueByOneDay_ReturnsCorrectFine() {
		//arrange
		loan = new Loan(book, patron, Calendar.getInstance().getDate(), ILoan.LoanState.OVER_DUE, loanId);
		patronloans.put(loan.getId(), loan);
		
		loans.put(loan.getId(), loan);
		currentLoans.put(loan.getId(), loan);
		
		int daysOverdue = 1;
		
		//increment date to a single day after due date
		Calendar.getInstance().incrementDate(daysOverdue);
		double expected = daysOverdue * ILibrary.FINE_PER_DAY;
		
		assertEquals(1.0, expected);
		
		//act
		double overdueFines = library.calculateOverDueFine(loan);
		
		//assert
		assertEquals(expected, overdueFines);
	}

}
