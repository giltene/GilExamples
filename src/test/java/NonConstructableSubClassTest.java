import org.junit.Test;

import java.util.Observable;

import static java.lang.Long.valueOf;
import static java.lang.Integer.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class NonConstructableSubClassTest {
    @Test
    public void shouldCreateSubClassInstanceWithDefaultParams() throws NoSuchMethodException {
        final NonConstructableSubClass<Long> instance =
                NonConstructableSubClass.newInstance(Long.class, 5);
        assertThat(valueOf(instance.getFieldA()), is(5));
        assertThat(valueOf(instance.getFieldB()), is(7));
        assertTrue(instance.getMemberClass() == Long.class);

    }

    @Test
    public void shouldCreateSubClassInstanceWithParams() throws NoSuchMethodException {
        final NonConstructableSubClass<Object> instance =
                NonConstructableSubClass.newInstance(Object.class, 5, 9);
        assertThat(valueOf(instance.getFieldA()), is(5));
        assertThat(valueOf(instance.getFieldB()), is(9));
        assertTrue(instance.getMemberClass() == Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToConstructWithDefaultParams() throws NoSuchMethodException {
        final NonConstructableSubClass<Object> instance =
                new NonConstructableSubClass<Object>(new Object(), Object.class, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToConstructWithParams() throws NoSuchMethodException {
        final NonConstructableSubClass<Object> instance =
                new NonConstructableSubClass<Object>(new Object(), Object.class, 5, 9);
    }

}
