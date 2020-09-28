def queryManager = session.workspace.queryManager;
def sqlQuery = "select [jcr:path], * from [nt:unstructured] as a where [sling:resourceType] = 'learnings/components/content/nested-multifield' and isdescendantnode(a, '/content/we-retail')";
def queryObj = queryManager.createQuery(sqlQuery, 'sql');
def queryResults = queryObj.execute();
def renamedCt = 0;

queryResults.nodes.each {
    node ->
        String nodePath = node.path;
        if(node.hasNode("outerMulti")){
            def nodeItr =node.getNodes(); 
            def navId="";
            def navName="";
            nodeItr.each{
            navTabsNode ->
                if(navTabsNode.getName().equals("outerMulti")){ // Multifield Node name
                    navTabsNode.getNodes().each{
                        item ->
                            if(item.hasProperty("navId")){
                                 navId = item.getProperty("navId").getValue().toString();
                                //println navId;
                            }
                            if(item.hasProperty("navName")){
                                navName = item.getProperty("navName").getValue().toString();
                                //println navName;
                            }
                            if(!node.hasNode(navId) && node.hasNode(navName)){
                                println "Node at path = " + node.path + " needs update"
	                            def srcPath = node.path + "/" + navName;
	                            def destPath = node.path + "/" + navId;
	                            node.getSession().move(srcPath, destPath);
	                            node.getSession().save();
	                            renamedCt = renamedCt + 1;
	                            println srcPath + " renamed to " + destPath;
                            }
                        }
                     }
                
                }   
            }
        
}
if(renamedCt == 0){
    println "No node needs update !!"
}