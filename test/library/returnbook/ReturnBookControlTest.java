package library.returnbook;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.Book;
import library.entities.Calendar;
import library.entities.IBook;
import library.entities.ILibrary;
import library.entities.ILoan;
import library.entities.IPatron;
import library.entities.Library;
import library.entities.Loan;
import library.entities.Patron;
import library.entities.helpers.IBookHelper;
import library.entities.helpers.ILoanHelper;
import library.entities.helpers.IPatronHelper;

@ExtendWith(MockitoExtension.class)
class ReturnBookControlTest {

	@Mock IReturnBookUI returnBookUI;
	IReturnBookControl returnBookControl;
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
		
		returnBookControl = new ReturnBookControl(library);
		returnBookControl.setUI(returnBookUI);
	}

	@Test
	void returnBookControl_WhenFineIncurred_AccruesCorrectFineToPatron() {
		//arrange
		loan = new Loan(book, patron, Calendar.getInstance().getDate(), ILoan.LoanState.OVER_DUE, loanId);
		patronloans.put(loan.getId(), loan);
		
		loans.put(loan.getId(), loan);
		currentLoans.put(loan.getId(), loan);
		
		boolean isDamaged = false;
		
		//increment date to two day after due date
		int daysOverdue = 2;
		Calendar.getInstance().incrementDate(daysOverdue);
		
		double finesPayable = patron.getFinesPayable();
		double expectedBeforeDischarge = 0.0;
		assertEquals(expectedBeforeDischarge, finesPayable);

		double expectedAfterDischarge = 2.0;
		//act
		returnBookControl.bookScanned(bookId);
		double finesPayableBeforeDischarge = patron.getFinesPayable();
		returnBookControl.dischargeLoan(isDamaged);
		finesPayable = patron.getFinesPayable();
		
		//assert
		assertEquals(expectedBeforeDischarge, finesPayableBeforeDischarge);
		assertEquals(expectedAfterDischarge, finesPayable);
	}

}
