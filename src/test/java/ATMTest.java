import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class ATMTest {

    @Before
    public void init(){
        ATM.validDenominations = Arrays.stream(new String[]{"1", "2", "5", "10", "20"})
                .map(Integer::valueOf)
                .collect(Collectors.toSet());
    }

    @Test
    public void depositOne() {

        ATM atm = new ATM();
        atm.deposit(new String[] {"10s: 8", "5s: 20"});

        assertEquals(Integer.valueOf(180), atm.getTotalAmount());
        assertEquals(Integer.valueOf(8), atm.getTotalCash().get(10));
        assertEquals(Integer.valueOf(20), atm.getTotalCash().get(5));
    }

    @Test
    public void depositTwo() {

        ATM atm = new ATM();
        atm.deposit(new String[] {"10s: 8", "5s: 20"});
        atm.deposit(new String[] {"20s: 3", "5s: 18", "1s: 4"});

        assertEquals(Integer.valueOf(334), atm.getTotalAmount());
        assertEquals(Integer.valueOf(4), atm.getTotalCash().get(1));
        assertEquals(Integer.valueOf(38), atm.getTotalCash().get(5));
        assertEquals(Integer.valueOf(8), atm.getTotalCash().get(10));
        assertEquals(Integer.valueOf(3), atm.getTotalCash().get(20));
    }

    @Test
    public void withdrawOne() {

        ATM atm = new ATM();
        atm.deposit(new String[] {"10s: 8", "5s: 20"});
        atm.deposit(new String[] {"20s: 3", "5s: 18", "1s: 4"});
        atm.withdraw(75);

        assertEquals(Integer.valueOf(259), atm.getTotalAmount());
        assertEquals(Integer.valueOf(4), atm.getTotalCash().get(1));
        assertEquals(Integer.valueOf(37), atm.getTotalCash().get(5));
        assertEquals(Integer.valueOf(7), atm.getTotalCash().get(10));
        assertEquals(Integer.valueOf(0), atm.getTotalCash().get(20));
    }

    @Test
    public void withdrawTwo() {

        ATM atm = new ATM();
        atm.deposit(new String[] {"10s: 8", "5s: 20"});
        atm.deposit(new String[] {"20s: 3", "5s: 18", "1s: 4"});
        atm.withdraw(75);
        atm.withdraw(122);

        assertEquals(Integer.valueOf(137), atm.getTotalAmount());
        assertEquals(Integer.valueOf(2), atm.getTotalCash().get(1));
        assertEquals(Integer.valueOf(27), atm.getTotalCash().get(5));
        assertEquals(Integer.valueOf(0), atm.getTotalCash().get(10));
        assertEquals(Integer.valueOf(0), atm.getTotalCash().get(20));
    }
}