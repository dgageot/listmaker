package net.gageot.listmaker;

import com.google.common.base.*;
import com.google.common.collect.*;
import org.fest.assertions.*;
import org.junit.*;

import java.util.*;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.asList;
import static net.gageot.listmaker.ListMaker.*;
import static org.fest.assertions.MapAssert.*;

public class ListMakerTest {
	@Test
	public void canMakeListFromIterable() {
		Iterable<String> iterable = Lists.newArrayList("A", "B", "C");

		List<String> list = with(iterable).toList();

		assertThat(list).containsSequence("A", "B", "C");
	}

	@Test
	public void canReturnSameListMakerAsTheOnePassed() {
		ListMaker<String> listMaker = new ListMaker<String>(Lists.newArrayList("A"));

		assertThat((Object) with(listMaker)).isSameAs(listMaker);
	}

	@Test
	public void canMakeListFromArray() {
		List<String> list = with("A", "B", "C").toList();

		assertThat(list).containsSequence("A", "B", "C");
	}

	@Test
	public void canMakeImutableList() {
		ImmutableList<String> list = with("A", "B", "C").toImmutableList();

		assertThat(list).containsSequence("A", "B", "C");
	}

	@Test
	public void canMakeIterableFromValues() {
		Iterable<String> list = with("A", "B", "C");

		assertThat(list).containsOnly("A", "B", "C");
	}

	@Test
	public void canJoinValues() {
		String joined = with("A", "B", "C").join(":");

		assertThat(joined).isEqualTo("A:B:C");
	}

	@Test
	public void canConvertToArray() {
		String[] array = with("A", "B", "C").toArray(String.class);

		assertThat(array).containsOnly("A", "B", "C");
	}

	@Test
	public void canKeepValuesBasedOnPredicate() {
		Iterable<String> list = with("A", "B", "C").only(Predicates.in(Lists.newArrayList("B")));

		assertThat(list).containsSequence("B");
	}

	@Test
	public void canExcludeValuesBasedOnPredicate() {
		Iterable<String> list = with("A", "B", "C").exclude(Predicates.in(Lists.newArrayList("B")));

		assertThat(list).containsSequence("A", "C");
	}

	@Test
	public void canExcludeValuesBasedOnClass() {
		Iterable<Superclass> list = with((Superclass) SubclassA.A, SubclassB.B).exclude(SubclassA.class);

		assertThat(list).containsOnly(SubclassB.B);
	}

	private interface Superclass {
		// Marker
	}

	private enum SubclassA implements Superclass {
		A
	}

	private enum SubclassB implements Superclass {
		B
	}

	@Test
	public void canExcludeOneValue() {
		Iterable<String> list = with("A", "B", "C").exclude("A");

		assertThat(list).containsSequence("B", "C");
	}

	@Test
	public void canExcludeValueList() {
		Iterable<String> list = with("A", "B", "C").exclude("A", "C");

		assertThat(list).containsSequence("B");
	}

	@Test
	public void canExcludeValues() {
		Iterable<String> list = with("A", "B", "C").exclude(Sets.newHashSet("A", "B"));

		assertThat(list).containsSequence("C");
	}

	@Test
	public void canKeepNonNullValues() {
		Iterable<String> list = with("A", null, "C").notNulls();

		assertThat(list).containsSequence("A", "C");
	}

	@Test
	public void canTransformValues() {
		Iterable<String> list = with(1, 2, 3).to(Functions.toStringFunction());

		assertThat(list).containsSequence("1", "2", "3");
	}

	@Test
	public void canConvertToSet() {
		Set<String> set = with("A", "A", "B", "B", "C", "C").toSet(Functions.<String>identity());

		assertThat(set).containsOnly("A", "B", "C");
	}

	@Test
	public void canConvertToImmutableSet() {
		ImmutableSet<String> set = with("A", "A", "B", "B", "C", "C").toImmutableSet();

		assertThat(set).containsOnly("A", "B", "C");
	}

	@Test
	public void canConvertToTreeSet() {
		Set<Integer> set = with("2", "3", "1", "1", "2", "3").toTreeSet(TO_INTEGER);

		assertThat(set).containsSequence(1, 2, 3);
	}

	@Test
	public void canConvertToTreeSetWithoutConversion() {
		Set<String> set = with("2", "3", "1", "1", "2", "3").toTreeSet();

		assertThat(set).containsSequence("1", "2", "3");
	}

	@Test
	public void canConvertToTreeSetWithComparator() {
		Set<String> set = with("2", "3", "1", "1", "2", "3").toTreeSet(Ordering.natural().reverse());

		assertThat(set).containsSequence("3", "2", "1");
	}

	@Test
	public void canGetFirst() {
		String first = with("A", "B", "C").first();

		assertThat(first).isEqualTo("A");
	}

	@Test(expected = NoSuchElementException.class)
	public void willFailToGetFirstElementOfEmptyList() {
		with(Lists.<String>newArrayList()).first();
	}

	@Test
	public void canUseADefaultValueVersionIfFirstIsNotFound() {
		assertThat(with().firstOrDefault("NOT FOUND")).isEqualTo("NOT FOUND");
		assertThat(with("FOUND").firstOrDefault("NOT FOUND")).isEqualTo("FOUND");

		assertThat(with().firstOrDefault(Predicates.alwaysTrue(), "NOT FOUND")).isEqualTo("NOT FOUND");
		assertThat(with("FOUND").firstOrDefault(Predicates.alwaysTrue(), "NOT FOUND")).isEqualTo("FOUND");

		assertThat(with().firstOrDefault(Functions.identity(), "FOUND", "NOT FOUND")).isEqualTo("NOT FOUND");
		assertThat(with("FOUND").firstOrDefault(Functions.identity(), "FOUND", "NOT FOUND")).isEqualTo("FOUND");
	}

	@Test
	public void canGetLast() {
		String last = with("A", "B", "C").getLast();

		assertThat(last).isEqualTo("C");
	}

	@Test(expected = NoSuchElementException.class)
	public void willFailToGetLastElementOfEmptyList() {
		with(Lists.<String>newArrayList()).getLast();
	}

	@Test
	public void canSort() {
		Iterable<String> sorted = with("C", "B", "A").sortOn(Ordering.<String>natural());

		assertThat(sorted).containsSequence("A", "B", "C");
	}

	@Test
	public void canSortBaseOnTransformation() {
		Iterable<String> sorted = with("90", "01", "300").sortOn(TO_INTEGER);

		assertThat(sorted).containsSequence("01", "90", "300");
	}

	@Test
	public void canCombineTransformations() {
		List<String> months = asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

		String juneToJuly = with(months).only(startsWith("J")).exclude("January").join(" to ");

		assertThat(juneToJuly).isEqualTo("June to July");
	}

	@Test
	public void canCountElements() {
		int count = with("C", "B", "A").size();

		assertThat(count).isEqualTo(3);
	}

	@Test
	public void canSearchValueUsingPredicate() {
		List<String> months = asList("January", "February", "March");

		boolean containsJanuary = with(months).contains(isEqualIgnoreCase("JANUARY"));
		boolean containsDecember = with(months).contains(isEqualIgnoreCase("DECEMBER"));

		assertThat(containsJanuary).isTrue();
		assertThat(containsDecember).isFalse();
	}

	@Test
	public void canFindMaximumValue() {
		List<Integer> values = asList(1, 15, 10);

		int max = with(values).max(Ordering.<Integer>natural());

		assertThat(max).isEqualTo(15);
	}

	@Test
	public void canFindMinimumValue() {
		List<Integer> values = asList(1, 15, 10);

		int min = with(values).min(Ordering.<Integer>natural());

		assertThat(min).isEqualTo(1);
	}

	@Test
	public void canCountUsingAPredicate() {
		List<String> months = asList("January", "February", "March");

		int count = with(months).count(startsWith("J"));

		assertThat(count).isEqualTo(1);
	}

	@Test
	public void canFilterOnASingleValues() {
		List<String> strings = Lists.newArrayList("1", "22", "333", "4444");

		Iterable<String> stringsOfLength2Or4 = with(strings).only(TO_LENGTH, 2);

		assertThat(stringsOfLength2Or4).containsExactly("22");
	}

	@Test
	public void canFilterOnValues() {
		List<String> strings = Lists.newArrayList("1", "22", "333", "4444");

		Iterable<String> stringsOfLength2Or4 = with(strings).only(TO_LENGTH, 2, 4);

		assertThat(stringsOfLength2Or4).containsExactly("22", "4444");
	}

	@Test
	public void canIndexBy() {
		List<String> strings = Lists.newArrayList("1", "22", "333");

		Map<Integer, String> stringByLength = with(strings).indexBy(TO_LENGTH);

		assertThat(stringByLength) //
				.includes(entry(1, "1")) //
				.includes(entry(2, "22")) //
				.includes(entry(3, "333"));
	}

	@Test
	public void canCheckIfEmpty() {
		assertThat(with(newArrayList("1", "2", "1")).isEmpty()).isFalse();
		assertThat(with(newArrayList("1")).isEmpty()).isFalse();
		assertThat(with(newArrayList()).isEmpty()).isTrue();
	}

	private static <T> ListAssert assertThat(Iterable<T> actual) {
		return Assertions.assertThat(ImmutableList.copyOf(actual));
	}

	private static ObjectAssert assertThat(Object actual) {
		return Assertions.assertThat(actual);
	}

	private static BooleanAssert assertThat(boolean actual) {
		return Assertions.assertThat(actual);
	}

	private static ObjectArrayAssert assertThat(Object[] actual) {
		return Assertions.assertThat(actual);
	}

	public static MapAssert assertThat(Map<?, ?> actual) {
		return Assertions.assertThat(actual);
	}

	private static Function<String, Integer> TO_LENGTH = new Function<String, Integer>() {
		@Override
		public Integer apply(String value) {
			return value.length();
		}
	};

	private static Function<String, Integer> TO_INTEGER = new Function<String, Integer>() {
		@Override
		public Integer apply(String value) {
			return Integer.parseInt(value);
		}
	};

	private static Predicate<String> startsWith(final String prefix) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String value) {
				return value.startsWith(prefix);
			}
		};
	}

	private static Predicate<String> isEqualIgnoreCase(final String expected) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String value) {
				return value.equalsIgnoreCase(expected);
			}
		};
	}
}
