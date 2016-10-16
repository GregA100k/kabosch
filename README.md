# kabosch
Kaggle Bosch Production Line Performance competition

First run was to read in the all the rows from the Numeric training data
and count up the 1's and 0's for each value

Second run was to take the count's of 1's and 0's and look for ratios
where the 1's to 0's for that value was greater than the overall ratio of
1's to 0's

Things to try next
==================
* Look for the same feature in different lines and/or stations
* look at the ranges of valus for different features to see if there
  are combinations of attribute values that may go together
* create a routine that pulls out display the value distribution
  for a specified feature  *created a way to get a sorted list of 
  values, at least for numerics.*
* build a way to test ranges of numeric values to find values more
  likely to indicate failures.

