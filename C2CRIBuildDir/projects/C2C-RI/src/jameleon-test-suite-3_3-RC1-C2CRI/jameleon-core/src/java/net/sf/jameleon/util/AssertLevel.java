/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A singleton that keeps track of the assert levels accross all test cases. This is implemented by calling an "assert" method
 * that takes an <code>assertLevel</code> parameter in it. If an "assert" method without that parameter is sent, this singleton will
 * no be used to check whether the assert can happen or not. The order of test levels follows:
 * <ol>
 *     <li>SMOKE
 *     <li>LEVEL1
 *     <li>LEVEL2
 *     <li>FUNCTION
 *     <li>LEVEL4
 *     <li>LEVEL5
 *     <li>REGRESSION
 *     <li>LEVEL7
 *     <li>LEVEL8
 *     <li>ACCEPTANCE
 * </ol>
 * The <code>LEVEL</code> are used when there is a test level that is somewhere between two of the test levels with meaning. It is suggested
 * that SMOKE, FUNCTION, REGRESSION, and ACCEPTANCE are exclusively used until a need is found for more levels.
 */
public class AssertLevel {

    /**
     * A Smoke level test. A Smoke test is usually run before QA even begins to start their testing.
     * This tells the tester that application is in some kind of functional state. In other words, these are tests that MUST pass in order for QA to
     * even bother further test.
     */
    public final static int SMOKE       = 0;
    /**
     * A test level between a Smoke and Function. Used just in case more levels are needed.
     */
    public final static int LEVEL1      = 1;
    /**
     * A test level between a Smoke and Function. Used just in case more levels are needed.
     */
    public final static int LEVEL2      = 2;
    /**
     * A Function level test. A Function test is usually run by QA to see if the application is mostly working correctly.
     * Usually these kinds of tests test that a feature works as documented.
     */
    public final static int FUNCTION    = 3;
    /**
     * A test level between a Function and Regression. Used just in case more levels are needed.
     */
    public final static int LEVEL4      = 4;
    /**
     * A test level between a Function and Regression. Used just in case more levels are needed.
     */
    public final static int LEVEL5      = 5;
    /**
     * A Regression level test. When a bug/feature is initially agreed upon, QA writes up tests to expose these.
     * These are then qualify as regression tests as they are used to prove that the software isn't regressing.
     */
    public final static int REGRESSION  = 6;
    /**
     * A test level between a Regression and Acceptance. Used just in case more levels are needed.
     */
    public final static int LEVEL7      = 7;
    /**
     * A test level between a Regression and Acceptance. Used just in case more levels are needed.
     */
    public final static int LEVEL8      = 8;
    /**
     * An Acceptance level test. Acceptance tests are usually very picky. They are the ultimate in proving that the application
     * is ready for the best customer experience.
     */
    public final static int ACCEPTANCE  = 9;
    /**
     * An invalid level. Used for validation.
     */
    public final static int INVALID_LEVEL = 100;
    /**
     * The default setting of every level
     */
    public final static int NO_LEVEL    = -1;

    protected int greaterThanLevel, lessThanLevel, level;
    protected Set levels = null;

    private static AssertLevel instance = null;

    /**
     * Since this is a singleton, a private constructor is in order.
     * Initializes all values to their defaults.
     */
    private AssertLevel(){
        clearAll();
    }

    /**
     * @return the instance available to everyone of AssertLevel
     */
    public static AssertLevel getInstance(){
        if (instance == null) {
            instance = new AssertLevel();
        }
        return instance;
    }
    /**
     * Clears any assert levels that may have been set.
     */
    public void clearAll() {
        levels = new HashSet();
        greaterThanLevel = NO_LEVEL;
        lessThanLevel = NO_LEVEL;
        level = NO_LEVEL;
    }
    /**
     * Tests whether an assert should be performed at a certain level. The greaterThanlevel and lessThanLevel are
     * actually greaterThanOrEqualTo and lessThanOrEqualTo. All possible combinations may be set and all will be checked.
     * @param assertLevel - The level the assert is going to be executed against.
     * @return true if the assert should be performed.
     */
    public boolean assertPerformable(int assertLevel){
        boolean perform = false;
        if ( levels.size() > 0 ) {
            Iterator it = levels.iterator();
            Integer i = null;
            while ( it.hasNext() && !perform ) {
                i = (Integer)it.next();
                perform = ( i.intValue() == assertLevel );
            }
        }
        if ( level != NO_LEVEL && assertLevel == level ) {
            perform = true;
        }
        if ( greaterThanLevel != NO_LEVEL && assertLevel >= greaterThanLevel ) {
            perform = true;
        }
        if ( lessThanLevel != NO_LEVEL && assertLevel <= lessThanLevel ) {
            perform = true;
        }
        if ( lessThanLevel == NO_LEVEL && 
             greaterThanLevel == NO_LEVEL &&
             level == NO_LEVEL &&
             levels.size() == 0){
            perform = true;
        }
        if (assertLevel == NO_LEVEL) {
            perform = true;
        }
        return perform;
    }

    /**
     * @return The level at which asserts with set to or greater than the value this is set may be executed.
     */
    public int getGreaterThanLevel(){
        return greaterThanLevel;
    }

    /**
     * @return The level at which asserts with levels set to or less than the value this is set may be executed.
     */
    public int getLessThanLevel(){
        return lessThanLevel;
    }

    /**
     * @return The level at which asserts with levels set to this may be executed.
     */
    public int getLevel(){
        return level;
    }

    /**
     * @return The levels at which asserts with levels set to this may be executed.
     */
    public Set getLevels(){
        return levels;
    }

    /**
     * Sets the level at which asserts with set to or greater than the value this is set may be executed.
     * @param greaterThanLevel -  The level at which asserts with set to or greater than the value this is set may be executed.
     */
    public void setGreaterThanLevel(int greaterThanLevel){
        this.greaterThanLevel = greaterThanLevel;
    }

    /**
     * Sets the level at which asserts with levels set to or less than the value this is set may be executed.
     * @param lessThanLevel - The level at which asserts with levels set to or less than the value this is set may be executed.
     */
    public void setLessThanLevel(int lessThanLevel){
        this.lessThanLevel = lessThanLevel;
    }

    /**
     * Sets the level at which asserts with levels set to this may be executed.
     * @param level - The level at which asserts with levels set to this may be executed.
     */
    public void setLevel(int level){
        this.level = level;
    }

    /**
     * Adds at level at which asserts with levels set to this may be executed to the list of possible levels.
     * @param level - The level at which asserts with levels set to this may be executed.
     */
    public void addLevel(int level){
        levels.add(new Integer(level));
    }

    /**
     * Removes at level at which asserts with levels set to this may be executed to the list of possible levels.
     * @param level - The level at which asserts with levels set to this may be executed.
     */
    public void removeLevel(int level){
        levels.remove(new Integer(level));
    }
}
