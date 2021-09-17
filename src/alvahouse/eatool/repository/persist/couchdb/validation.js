function(newDoc, oldDoc, userCtx, secObj) { 
	
	// Allow admins to write.  Needed when initialising the database.
	if(userCtx.roles.indexOf('_admin') != -1) {
		return;
	}
	
	if(newDoc.type_name) {
		
		// Map of role required to update different types.
		var required = {
				// Basic meta model
				MetaEntityDto: "designer",
				MetaRelationshipDto: "designer",
				ControlledListTypeDto: "designer",
				RegexpCheckedTypeDto: "designer",
				TimeSeriesTypeDto: "designer",
				
				// Basic model
				EntityDto: "contributor",
				RelationshipDto: "contributor",
				
				// Diagrams
				StandardDiagramTypeDto : "designer",
				TimeDiagramTypeDto : "designer",
				StandardDiagramDto: "contributor",
				TimeDiagramDto: "contributor",
				
				// Import/export mappings
				ExportMappingDto : "designer",
				ImportMappingDto : "designer",
				
				// Misc
				HTMLPageDto: "contributor",
				ImageDto: "contributor",
				ScriptDto: "designer",
					
		};
		
		var requiredRole = required[newDoc.type_name];
		if(requiredRole){
			if(userCtx.roles.indexOf(requiredRole) == -1) {
				throw( { unauthorized : "Operation requires " + requiredRole + " role"} );
			}
		} else {
			log("type " + newDoc.type_name + " update has no required role");
		}
		
	} else {
		throw( {forbidden : "Missing type_name field"});
	}
}