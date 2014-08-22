var purchasesControllers = angular
		.module(
				"purchasesControllers",
				[],
				function($compileProvider) {
					$compileProvider
							.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file|javascript):/);
				});

purchasesControllers.controller("PurchaseController", function($scope,
		$http, $routeParams) {
	$scope.filterFields = {};
	populatePurchaseData($scope, $http, $routeParams, {});
	$scope.edited = function(index) {
		var purchase = $scope.purchasesList[index];
		if(purchase.amount) {
			purchase.amount = parseFloat(purchase.amount);
		}
		else {
			purchase.amount = 0;
		}
		purchase.account = populateEmpty(purchase.account);
		purchase.archiveref = populateEmpty(purchase.archiveref);
		purchase.textcode = populateEmpty(purchase.textcode);
		$scope.purchasesList[index] = purchase
		$http.post("/purchases/edit/" + $scope.purchasesList[index]._id,
				$scope.purchasesList[index], {})
		.success(function(data, stat, heads, cnfg) {
			$scope.purchaseSum = calculateSum($scope); 
		})
		.error(function(data, stat, heads, cnfg) {
			alert("Editering feilet!");
		});
	};
	$scope.deletePurchase = function(index) {
		var purchase = $scope.purchasesList[index];
		if(confirm("Er du sikker på du vil slette " + purchase.description + "?")){
			var responsePromise = $http.delete("/purchases/delete/" + purchase._id, {});
			responsePromise.success(function(dataFromServer, status, headers, config) {
				$scope.purchasesList.splice(index, 1);
			});
			responsePromise.error(function(data, status, headers, config) {
				alert("Sletting feilet!");
			});
		}
	 }
	$scope.filter = function(filterFields){
		$scope.filterFields = filterFields;
		populatePurchaseData($scope, $http, $routeParams);
	}
});

function populatePurchaseData($scope, $http, $routeParams) {
	/*
	var url = '/purchases/list';
	$http.get(url).success(function(data, status, headers, config) {
		$scope.expenseDetails = data.result.expDetList;
		$scope.expenseTypes = data.result.expTypesList;
		$scope.purchasesList = data.result.purchasesList;
		$scope.purchaseSum = calculateSum($scope);
		
	});
	*/
	$http.get('/expenseTypes/list').success(function(data, status, headers, config) {
		$scope.expenseTypes = data.expDetList;
		populateFormDataFromParams($scope, $routeParams);
	
		var url = "/purchases/list?" + "page=" + 0;
		if($scope.filterFields.expType) {
			url += "&expType=" + $scope.filterFields.expType;
		}
		if($scope.filterFields.expDet) {
			url += "&expDet=" + $scope.filterFields.expDet;
		}
		if($scope.filterFields.start) {
			url += "&start=" + $scope.filterFields.start;
		}
		if($scope.filterFields.end) {
			url += "&stop=" + $scope.filterFields.end;
		}
		$http.get(url).success(function(data, status, headers, config) {
			$scope.expenseDetails = data.result.expDetList;
			$scope.purchasesList = data.result.purchasesList;
			$scope.purchaseSum = calculateSum($scope);
		});
	});
	
}
function calculateSum($scope){
	var sum = 0;
	if(!$scope.purchasesList){
		return 0;
	}
	for(var i = 0; i < $scope.purchasesList.length; i++) {
		var p =  $scope.purchasesList[i];
		sum += p.amount;
	}
	return sum;
}
function populateEmpty(field){
	if(!field){ return ""; }
}
function populateFormDataFromParams($scope, $routeParams){
	
	var et = $routeParams.expType;
	var start = $routeParams.start;
	var end = $routeParams.stop;
	for(var i = 0; i < $scope.expenseTypes.length; i++){
		if($scope.expenseTypes[i]._id == et) {
			console.log("Treff");
			$scope.filterFields.expType = $scope.expenseTypes[i]._id;
			break;
		}
	}
	
	$scope.filterFields.start = createDate(start);
	$scope.filterFields.end = createDate(end);
	console.log($scope.filterFields + " " + et + " " + $scope.filterFields.star + " " + $scope.filterFields.end);
}

function createDate(dateAsLong) {
	if(!dateAsLong) {
		return "";
	}
	var daten = new Date(parseFloat(dateAsLong));
    var yyyy = daten.getFullYear().toString();                                    
    var mm = (daten.getMonth()+1).toString(); // getMonth() is zero-based         
    var dd  = daten.getDate().toString();             
                        
    var dateString =  (dd[1]?dd:"0"+dd[0]) + '.' + (mm[1]?mm:"0"+mm[0]) + '.' +yyyy ;
    console.log(dateString);
    
    return dateString;
};  