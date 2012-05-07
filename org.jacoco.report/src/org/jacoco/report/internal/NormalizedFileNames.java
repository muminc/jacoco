/*******************************************************************************
 * Copyright (c) 2009, 2012 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.report.internal;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Internal utility to create normalized file names from string ids. The file
 * names generated by an instance of this class have the following properties:
 * 
 * <ul>
 * <li>The same input id is mapped to the same file name.</li>
 * <li>Different ids are mapped to different file names.</li>
 * <li>For safe characters the file name corresponds to the input id, other
 * characters are replaced by <code>_</code> (underscore).</li>
 * <li>File names are case aware, i.e. the same file name but with different
 * upper/lower case characters is not possible.</li>
 * <li>If unique filenames can't directly created from the ids, additional
 * suffixes are appended.</li>
 * </ul>
 */
class NormalizedFileNames {

	private static final BitSet LEGAL_CHARS = new BitSet();

	static {
		final String legal = "abcdefghijklmnopqrstuvwxyz"
				+ "ABCDEFGHIJKLMNOPQRSTUVWYXZ0123456789$-._";
		for (final char c : legal.toCharArray()) {
			LEGAL_CHARS.set(c);
		}
	}

	private final Map<String, String> mapping = new HashMap<String, String>();

	private final Set<String> usedNames = new HashSet<String>();

	public String getFileName(final String id) {
		String name = mapping.get(id);
		if (name != null) {
			return name;
		}
		name = replaceIllegalChars(id);
		name = ensureUniqueness(name);
		mapping.put(id, name);
		return name;
	}

	private String replaceIllegalChars(final String s) {
		final StringBuilder sb = new StringBuilder(s.length());
		boolean modified = false;
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if (LEGAL_CHARS.get(c)) {
				sb.append(c);
			} else {
				sb.append('_');
				modified = true;
			}
		}
		return modified ? sb.toString() : s;
	}

	private String ensureUniqueness(final String s) {
		String unique = s;
		String lower = unique.toLowerCase(Locale.ENGLISH);
		int idx = 1;
		while (usedNames.contains(lower)) {
			unique = s + '~' + idx++;
			lower = unique.toLowerCase(Locale.ENGLISH);
		}
		usedNames.add(lower);
		return unique;
	}

}
