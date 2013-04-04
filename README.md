pdfsplit-maven-plugin
=====================

pdfsplit-maven-plugin is a simple plugin for maven that provides a way for your maven builds to grab pieces of larger PDF documents and package these with your product.

Example
-------
Add the repository
```xml
<pluginRepositories>
  <pluginRepository>
    <id>pdfsplit-plugin</id>
    <url>https://raw.github.com/zch/maven/releases/</url>
  </pluginRepository>
</pluginRepositories>
```

Add the plugin
```xml
<plugin>
  <groupId>com.github.zch</groupId>
  <artifactId>pdfsplit-maven-plugin</artifactId>
  <version>1.0.1</version>
  <executions>
    <execution>
      <id>pdfsplit</id>
      <phase>package</phase>
      <goals>
        <goal>split</goal>
      </goals>
      <configuration>
          <inputUrl>https://vaadin.com/download/book-of-vaadin/vaadin-7/pdf/book-of-vaadin.pdf</inputUrl>
          <chapter>Chapter 22. Mobile Applications with TouchKit</chapter>
<!--          <pageRange>479-506</pageRange>-->
          <outFile>${project.build.directory}/chapter.pdf</outFile>
      </configuration>
    </execution>
  </executions>
</plugin>
```

The parameters are:
- inputUrl: The URL for the PDF document from which to extract something
- outFile: The file where the extracted pages should be stored
- chapter: The name of the chapter to extract, e.g. "Chapter 22. Mobile Applications with TouchKit" to extract that chapter from Book of Vaadin
- pageRange: A range of pages given as start-end, e.g. "479-506", this parameter will only be used if no chapter is given

Generally you will want to specify either the chapter parameter or the pageRange parameter, not both as only the chapter parameter will be respected in that case.