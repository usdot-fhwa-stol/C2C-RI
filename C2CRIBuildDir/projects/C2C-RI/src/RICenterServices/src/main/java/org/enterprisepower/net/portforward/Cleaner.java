/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.enterprisepower.net.portforward;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Kenneth Xu
 * 
 */
public class Cleaner implements Runnable {
	private static final Log log = LogFactory.getLog(Cleaner.class);

	List<Cleanable> list = new ArrayList<Cleanable>();

	public void run() {
		cleanup();
	}

	public synchronized void cleanup() {
		while (true) {
			for (Iterator<Cleanable> itr = list.iterator(); itr.hasNext();) {
				Cleanable p = itr.next();
				if (p.isCompleted()) {
					p.close();
					itr.remove();
				}
			}
			try {
				wait(3000);
			} catch (InterruptedException e) {
				log.error("Cleaner Process Error in cleanup method: "+e.getMessage(), e);
			}
		}
	}

	public synchronized void add(Cleanable p) {
		list.add(p);
	}

}
