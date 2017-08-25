// thus http://jenkins:8000/ is the URL
'hg -R /hg --config web.allow_push=* --config web.push_ssl=False serve -d'.execute()
// http://jenkins:8001/
'hg -R /pipeline-lib --config web.allow_push=* --config web.push_ssl=False serve -d -p 8001'.execute()
