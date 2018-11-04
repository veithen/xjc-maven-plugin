# xjc-maven-plugin

This is a simple (i.e. not feature complete) alternative to [jaxb2-maven-plugin](https://www.mojohaus.org/jaxb2-maven-plugin/Documentation/v2.4.1/), [maven-jaxb2-plugin](https://github.com/highsource/maven-jaxb2-plugin) and [cxf-xjc-plugin](http://cxf.apache.org/cxf-xjc-plugin.html). It grew out of the need to have a Maven plugin for xjc satisfying the following requirements:

*   It must have separate goals to generate main and test sources (maven-jaxb2-plugin does not) and generated test sources must be added correctly to the Maven project (jaxb2-maven-plugin [does not](http://svn.apache.org/viewvc/axis/axis2/java/core/trunk/modules/metadata/pom.xml?r1=1815130&r2=1815129&pathrev=1815130)).

*   It must works out of the box with recent Java versions (Neither [jaxb2-maven-plugin](https://www.mail-archive.com/mojohaus-dev@googlegroups.com/msg00443.html) nor cxf-xjc-plugin do).

*   It shouldn't fork a new VM to invoke xjc.
