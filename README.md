# Ontopia JDO

This project aims to replace the current Ontopia RDBMS implementation by using the JDO specification. 

## Usage

The main entry classes are located in the `net.ontopia.topicmaps.impl.jdo.entry` package and operate like 
other sources. A source can be instantiated programatically or by using a sources xml file.

### Via tm-sources.xml

You can add a JDO topicmap source by defining it in a sources xml file like:

    <source class="net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapSource">
      <param name="id" value="jdo"/>
      <param name="title" value="JDO"/>
      <param name="supportsCreate" value="true"/>
      <param name="supportsDelete" value="true"/>
      <param name="propertyFile" value="${CWD}/db.properties"/>
    </source>

Supported properties of the source are: 

- `id`: The unique identification of the source
- `title`: The human friendly title of the source
- `propertyFile`: A reference to a property file containing configuration for JDO / datanucleus. 
The `JDOTopicMapSource` uses `StreamUtils.getInputStream()` to locate the file.
- `supportsCreate`: Is creation of new topicmaps allowed?
- `supportsDelete`: Is deletion of topicmaps allowed?


### Programatically

A `JDOTopicMapSource` may be instantiated programatically. The supported fields are equals to those used in the 
sources xml.

	JDOTopicMapSource source = new JDOTopicMapSource();
	source.setId("jdo");
	source.setTitle("JDO source");
	source.setSupportsCreate(true);
	source.setSupportsDelete(true);
	source.setPropertyFile("classpath:db.properties");
	
	// do something with source
	
	source.close();

Make sure to close the source when it is no longer needed to release all connections to the database.


## JDO configuration

This project uses Datanucleus as the JDO implementation of choice. In theory other JDO frameworks can be used, 
but only Datanucleus was tested in this project.

To configure datanucleus, a set of properties has to be profived by means of a property file:

	javax.jdo.option.ConnectionDriverName=org.h2.Driver
	javax.jdo.option.ConnectionURL=jdbc:h2:target/ontopia
	javax.jdo.option.ConnectionUserName=sa
	javax.jdo.option.ConnectionPassword=
	datanucleus.schema.autoCreateAll=true
	
The first four here are just like when using Ontopia RDBMS. The last property indicates that JDO is allowed to create 
any meta object in the database that it requires (tables, indices, etc).

## Goals

The goal of this project is to replace the Ontopia RDBMS connector to achieve:

- **Less and newer code**: the RDBMS packages are big, and most of the code is very old
- **More database support**: we no longer program/configure the supported databases, but benefit from the huge list of supported databases directly. 
- **External optimization**: optimization is (mostly) part of the JDO abstraction layer, which means we won't have to program it 
- **Use of open source community**: JDO and Datanucleus are maintained by a large open source community, which means we get improvements on each new version
- **Better integration**: extending Ontopia's datamodel with your own JDO persisted Pojos should now be possible.

## Roadmap

This project is the first beta version. It has been tested within the scope of Ontopia code, and has 
been tested by Morpheus by integration into existing projects. So far, only H2 and Postgresql have been
tested as backends.

We aim to have Ontopia JDO be part of the release candidates for Ontopia 6.0. A requirement of this is 
that more testing has to be done, in many more backends. 

