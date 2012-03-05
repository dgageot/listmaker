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

import java.util.*;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Iterables.*;

public class ListMaker<T> implements Iterable<T> {
	final Iterable<T> values;

	ListMaker(Iterable<T> values) {
		this.values = values;
	}

	public static <T> ListMaker<T> with(Iterable<T> values) {
		if (values instanceof ListMaker<?>) {
			return (ListMaker<T>) values;
		}
		return new ListMaker<T>(values);
	}

	@SuppressWarnings("unchecked")
	public static <T> ListMaker<T> with() {
		return new ListMaker<T>(Arrays.<T>asList());
	}

	public static <T> ListMaker<T> with(T... values) {
		return new ListMaker<T>(Arrays.asList(values));
	}

	public ListMaker<T> only(Predicate<? super T> filter) {
		if (filter == Predicates.alwaysTrue()) {
			return this;
		}
		return with(filter(values, filter));
	}

	public <P> ListMaker<T> only(Function<? super T, P> equalTo, P valueToCompareWith) {
		return only(whereEquals(equalTo, valueToCompareWith));
	}

	@SuppressWarnings("unchecked")
	public <P> ListMaker<T> only(Function<? super T, P> equalTo, P... valuesToCompareWith) {
		List<Predicate<? super T>> allWhereEquals = Lists.newArrayList();

		for (P value : valuesToCompareWith) {
			Predicate<? super T> whereEquals = whereEquals(equalTo, value);
			allWhereEquals.add(whereEquals);
		}

		return only(or(allWhereEquals.toArray(new Predicate[allWhereEquals.size()])));
	}

	public <P> ListMaker<T> only(Function<? super T, P> transform, Predicate<? super P> filter) {
		return only(compose(filter, transform));
	}

	public T first(Predicate<? super T> predicate) {
		return Iterables.find(values, predicate);
	}

	public T firstOrDefault(T defaultValue) {
		try {
			return first();
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}

	public T firstOrDefault(Predicate<? super T> predicate, T defaultValue) {
		try {
			return first(predicate);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}

	public <P> T firstOrDefault(Function<? super T, P> equalTo, P value, T defaultValue) {
		try {
			return first(equalTo, value);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}

	public <P> T first(Function<? super T, P> equalTo, P value) {
		return first(whereEquals(equalTo, value));
	}

	public T first() {
		try {
			return Iterables.get(values, 0);
		} catch (IndexOutOfBoundsException e) {
			throw new NoSuchElementException();
		}
	}

	public boolean contains(Predicate<? super T> predicate) {
		return Iterables.any(values, predicate);
	}

	public <P> boolean contains(Function<? super T, P> equalTo, P value) {
		return contains(whereEquals(equalTo, value));
	}

	public <S extends T> ListMaker<T> exclude(Class<S> subClass) {
		return only(not(Predicates.instanceOf(subClass)));
	}

	public ListMaker<T> exclude(Predicate<? super T> filter) {
		return only(not(filter));
	}

	public <P> ListMaker<T> exclude(Function<? super T, P> transform, Predicate<? super P> filter) {
		return only(not(compose(filter, transform)));
	}

	public ListMaker<T> exclude(T... excludeValues) {
		return only(not(in(Lists.newArrayList(excludeValues))));
	}

	public ListMaker<T> exclude(Collection<T> excludeValues) {
		return only(not(in(excludeValues)));
	}

	public int count(Predicate<? super T> filter) {
		return only(filter).size();
	}

	public <P> int count(Function<? super T, P> equalTo, P value) {
		return only(whereEquals(equalTo, value)).size();
	}

	public ListMaker<T> sortOn(Ordering<? super T> ordering) {
		return with(ordering.sortedCopy(values));
	}

	public ListMaker<T> sortOn(Comparator<? super T> comparator) {
		return sortOn(Ordering.from(comparator));
	}

	public ListMaker<T> sortOn(Function<? super T, ? extends Comparable<?>> transform) {
		return sortOn(Ordering.natural().onResultOf(transform));
	}

	public ListMaker<T> notNulls() {
		return only(Predicates.<T>notNull());
	}

	public <R> ListMaker<R> to(Function<? super T, R> transform) {
		return with(transform(values, transform));
	}

	public <R, C extends Iterable<R>> ListMaker<R> flatMap(Function<? super T, C> transform) {
		return with(Iterables.concat(to(transform)));
	}

	public T max(Ordering<? super T> ordering) {
		return ordering.max(values);
	}

	public T min(Ordering<? super T> ordering) {
		return ordering.min(values);
	}

	public <V extends Comparable<V>> T maxOnResultOf(Function<? super T, V> function) {
		return Ordering.natural().onResultOf(function).max(values);
	}

	public <V extends Comparable<V>> T minOnResultOf(Function<? super T, V> function) {
		return Ordering.natural().onResultOf(function).min(values);
	}

	public List<T> toList() {
		return Lists.newArrayList(values);
	}

	public ImmutableList<T> toImmutableList() {
		return ImmutableList.copyOf(values);
	}

	public String join(String separator) {
		return Joiner.on(separator).join(values);
	}

	public T[] toArray(Class<T> type) {
		return Iterables.toArray(values, type);
	}

	public Set<T> toSet() {
		return Sets.newHashSet(values);
	}

	public <R> Set<R> toSet(Function<? super T, R> transform) {
		return Sets.newHashSet(to(transform));
	}

	public TreeSet<T> toTreeSet() {
		TreeSet<T> set = new TreeSet<T>();
		Iterables.addAll(set, this);
		return set;
	}

	public TreeSet<T> toTreeSet(Comparator<? super T> c) {
		TreeSet<T> set = new TreeSet<T>(c);
		Iterables.addAll(set, this);
		return set;
	}

	public <R> TreeSet<R> toTreeSet(Function<? super T, R> transform, Comparator<? super R> ordering) {
		TreeSet<R> set = new TreeSet<R>(ordering);
		Iterables.addAll(set, to(transform));
		return set;
	}

	public <R extends Comparable<R>> TreeSet<R> toTreeSet(Function<? super T, R> transform) {
		return toTreeSet(transform, null);
	}

	public ImmutableSet<T> toImmutableSet() {
		return ImmutableSet.copyOf(values);
	}

	public T getLast() {
		return Iterables.getLast(values);
	}

	public int size() {
		return Iterables.size(values);
	}

	public <K> Map<K, T> indexBy(Function<? super T, ? extends K> toKey) {
		Map<K, T> map = Maps.newHashMap();

		for (T value : values) {
			map.put(toKey.apply(value), value);
		}

		return map;
	}

	@Override
	public Iterator<T> iterator() {
		return values.iterator();
	}

	public boolean isEmpty() {
		return Iterables.isEmpty(values);
	}

	private <P> Predicate<? super T> whereEquals(Function<? super T, P> equalTo, P value) {
		return compose(equalTo(value), equalTo);
	}

	@Override
	public boolean equals(Object o) {
		return this == o || o instanceof Iterable<?> && Iterables.elementsEqual(this, (Iterable<?>) o);
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public String toString() {
		return values.toString();
	}
}
