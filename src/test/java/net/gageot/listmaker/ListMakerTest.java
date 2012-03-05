/*
 * This file is part of ListMaker.
 *
 * Copyright (C) 2012
 * "David Gageot" <david@gageot.net>,
 *
 * ListMaker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ListMaker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ListMaker.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		Iterable<String> iterable = Arrays.asList("A", "B", "C");

		List<String> list = with(iterable).toList();

		assertThat(list).containsSequence("A", "B", "C");
	}

	@Test
	public void canReturnSameListMakerAsTheOnePassed() {
		ListMaker<String> listMaker = new ListMaker<String>(Arrays.asList("A"));

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
		Iterable<String> list = with("A", "B", "C").only(Predicates.in(Arrays.asList("B")));

		assertThat(list).containsSequence("B");
	}

	@Test
	public void canExcludeValuesBasedOnPredicate() {
		Iterable<String> list = with("A", "B", "C").exclude(Predicates.in(Arrays.asList("B")));

		assertThat(list).containsSequence("A", "C");
	}

	@Test
	public void canKeepValuesBasedOnFunctionAndPredicate() {
		Iterable<String> list = with("1", "2", "3").only(TO_INTEGER, Predicates.in(Arrays.asList(1)));

		assertThat(list).containsOnly("1");
	}

	@Test
	public void canExcludeValuesBasedOnFunctionAndPredicate() {
		Iterable<String> list = with("1", "2", "3").exclude(TO_INTEGER, Predicates.in(Arrays.asList(1)));

		assertThat(list).containsOnly("2", "3");
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
		Set<String> set = with("A", "A", "B", "B", "C", "C").toSet();

		assertThat(set).containsOnly("A", "B", "C");
	}

	@Test
	public void canConvertToSetUsingTransformation() {
		Set<String> set = with("A", "a", "B", "b", "C", "c").toSet(TO_UPPERCASE);

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
	public void canSortWitComparator() {
		Iterable<String> sorted = with("C", "B", "A").sortOn(new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareTo(s2);
			}
		});

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
	public void canSearchValueUsingFunction() {
		List<String> months = asList("January", "February", "March");

		boolean containsJanuary = with(months).contains(TO_UPPERCASE, "JANUARY");
		boolean containsDecember = with(months).contains(TO_UPPERCASE, "DECEMBER");

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
	public void canFindMaximumValueUsingTransformation() {
		List<String> values = asList("0001", "15", "100");

		String max = with(values).maxOnResultOf(TO_INTEGER);

		assertThat(max).isEqualTo("100");
	}

	@Test
	public void canFindMinimumValue() {
		List<Integer> values = asList(1, 15, 10);

		int min = with(values).min(Ordering.<Integer>natural());

		assertThat(min).isEqualTo(1);
	}

	@Test
	public void canFindMinimumValueUsingTransformation() {
		List<String> values = asList("0001", "15", "100");

		String max = with(values).minOnResultOf(TO_INTEGER);

		assertThat(max).isEqualTo("0001");
	}

	@Test
	public void canCountUsingAPredicate() {
		List<String> months = asList("January", "February", "March");

		int count = with(months).count(startsWith("J"));

		assertThat(count).isEqualTo(1);
	}

	@Test
	public void canCountUsingAFunction() {
		List<String> months = asList("January", "February", "March");

		int count = with(months).count(TO_UPPERCASE, "JANUARY");

		assertThat(count).isEqualTo(1);
	}

	@Test
	public void canFilterOnASingleValues() {
		List<String> strings = Arrays.asList("1", "22", "333", "4444");

		Iterable<String> stringsOfLength2Or4 = with(strings).only(TO_LENGTH, 2);

		assertThat(stringsOfLength2Or4).containsExactly("22");
	}

	@Test
	public void canFilterOnValues() {
		List<String> strings = Arrays.asList("1", "22", "333", "4444");

		Iterable<String> stringsOfLength2Or4 = with(strings).only(TO_LENGTH, 2, 4);

		assertThat(stringsOfLength2Or4).containsExactly("22", "4444");
	}

	@Test
	public void canIndexBy() {
		List<String> strings = Arrays.asList("1", "22", "333");

		Map<Integer, String> stringByLength = with(strings).indexBy(TO_LENGTH);

		assertThat(stringByLength) //
				.includes(entry(1, "1")) //
				.includes(entry(2, "22")) //
				.includes(entry(3, "333"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void canFlatMap() {
		List<List<String>> listOfLists = Arrays.asList(Arrays.asList("1", "2"), Arrays.asList("3", "4", "5"));

		ListMaker<String> values = with(listOfLists).flatMap(Functions.<Iterable<String>>identity());

		assertThat(values).containsExactly("1", "2", "3", "4", "5");
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

	private static Function<String, String> TO_UPPERCASE = new Function<String, String>() {
		@Override
		public String apply(String value) {
			return value.toUpperCase();
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
