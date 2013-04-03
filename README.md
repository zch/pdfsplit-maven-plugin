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
  <version>1.0-SNAPSHOT</version>
  <executions>
    <execution>
      <id>pdfsplit</id>
      <phase>package</phase>
      <goals>
        <goal>split</goal>
      </goals>
      <configuration>
          <inputUrl>https://vaadin.com/download/book-of-vaadin/vaadin-7/pdf/book-of-vaadin.pdf</inputUrl>
          <pageRange>479-506</pageRange>
          <outFile>${project.build.directory}/chapter.pdf</outFile>
      </configuration>
    </execution>
  </executions>
</plugin>
```