newspaper-statistics
==========================

Provides functionality for generating statistics for a batch as xml, and accessing these through a web frontend.

## Installation
When the tar.gz is unpacked a web folder will appear. This contains a statistics folder, which contains the statistics
frontend files. The statistics.html file contain the GUI. the statistics xml files must be place in a data dir in the
statistics folder. This can either be done directly by defining the statistics.outputdir to point here or by creating a
symlink to the folder containing the xml files.

## Configuration

In addition to the standard configuration parameters for autonomous components, this component requires the following
properties (values given below are dummy-examples):
    # statistics.outputdir: Where to place the generated statistics.
    # statistics.zeroaccuracy.ignore: Should alto zero values be included in the accuracy mean. Default is that
    false.


## Design
The statistics generation is implemented as a number of StatisticsCollector subclasses, one type for each batch node type.
 The collectors have two primary responsibilities.
 1. Process node events into determining what the new node type is a creating and returning a associated collector type.
 2. Collecting the actual statistics and writing these to through StatisticWriter. When a node has finished it's collector
  will add is statistics to that of the parent.

Most of the booking for this is placed in the parent StatisticsCollector class, only node specific behaviour is
implemented. Examples of this are:
* Define which children should be generated based on which events.
* Collect nonstandard statistics. Node counting is done by default.
* Parent-Child statistics conversion. If one kind of statistics need to be translated to another when passed to the
parent.
* As default nodes are always counted and written to the statistics. This can be disabled.

