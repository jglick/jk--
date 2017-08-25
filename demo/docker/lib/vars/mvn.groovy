def call(args) {
    def m3 = tool 'M3'
    configFileProvider([configFile(fileId: 'jenkins-mirror', variable: 'SETTINGS')]) {
        ansiColor('xterm') {
            sh "$m3/bin/mvn -s \$SETTINGS $args"
        }
    }
}
