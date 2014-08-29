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
	$scope.pagination = {};			
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
		console.log(purchase.textcode);
		var tcode = populateEmpty(purchase.textcode);
		console.log(tcode);
		purchase.textcode = populateEmpty(purchase.textcode);
		$scope.purchasesList[index] = purchase

		console.log($scope.purchasesList[index]);
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
	};

	$scope.resetDetails = function(){
		$scope.filterFields.expDet = "";
	}
	$scope.filter = function(filterFields){
		$scope.filterFields = filterFields;
		populatePurchaseData($scope, $http, $routeParams);
	}
	$scope.setCurrent = function(page){	
		if(page != $scope.pagination.current && page > 0 && page <= $scope.pagination.numPages) {			
			$scope.pagination.current = page;
			populatePurchaseData($scope, $http, $routeParams);
		}
	};
});

function populatePurchaseData($scope, $http, $routeParams) {
	
	$http.get('/expenseTypes/list').success(function(data, status, headers, config) {
		$scope.expenseTypes = data.expTypesList;
		populateFormDataFromParams($scope, $routeParams);
	
		var page = 0;		
		if($scope.pagination.current){
			page = $scope.pagination.current - 1;
		}
		var url = "/purchases/list?" + "page=" + page;
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
			$scope.expenseDetails = addDefaultOption(data.expDetList);
			
			$scope.purchasesList = data.purchasesList.items;
			$scope.purchaseSum = data.purchasesList.totalSum;
			$scope.pagination.page = data.purchasesList.page;
			$scope.pagination.offset = data.purchasesList.offset;
			$scope.pagination.totalSize = data.purchasesList.total;
			$scope.pagination.totalSum = data.purchasesList.totalSum;		
			$scope.pagination.current = data.purchasesList.page;	
			$scope.pagination.numPages = Math.ceil(data.purchasesList.total / 10);

			$scope.pagination.pages = generatePagesArray($scope.pagination.current, $scope.pagination.totalSize, 10, 9);
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
	if(!field){ 
		return ""; 
	}
	return field;
}
function populateFormDataFromParams($scope, $routeParams){
	
	var et = $routeParams.expType;
	var start = $routeParams.start;
	var end = $routeParams.stop;
	for(var i = 0; i < $scope.expenseTypes.length; i++){
		if($scope.expenseTypes[i]._id == et) {
			$scope.filterFields.expType = $scope.expenseTypes[i]._id;
			break;
		}
	}
	
	$scope.filterFields.start = createDate(start);
	$scope.filterFields.end = createDate(end);
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
    
    return dateString;
};  

function generatePagesArray(currentPage, collectionLength, rowsPerPage, paginationRange) {
    var pages = [];
    var totalPages = Math.ceil(collectionLength / rowsPerPage);
    var halfWay = Math.ceil(paginationRange / 2);
    var position;
    if (currentPage <= halfWay) {
        position = 'start';
    } else if (totalPages - halfWay < currentPage) {
        position = 'end';
    } else {
        position = 'middle';
    }
    var ellipsesNeeded = paginationRange < totalPages;
    var i = 1;
    while (i <= totalPages && i <= paginationRange) {
        var pageNumber = calculatePageNumber(i, currentPage, paginationRange, totalPages);
        var openingEllipsesNeeded = (i === 2 && (position === 'middle' || position === 'end'));
        var closingEllipsesNeeded = (i === paginationRange - 1 && (position === 'middle' || position === 'start'));
        if (ellipsesNeeded && (openingEllipsesNeeded || closingEllipsesNeeded)) {
            pages.push('...');
        } else {
            pages.push(pageNumber);
        }
        i ++;
    }
    return pages;
}
function calculatePageNumber(i, currentPage, paginationRange, totalPages) {
    var halfWay = Math.ceil(paginationRange/2);
    if (i === paginationRange) {
        return totalPages;
    } else if (i === 1) {
        return i;
    } else if (paginationRange < totalPages) {
        if (totalPages - halfWay < currentPage) {
            return totalPages - paginationRange + i;
        } else if (halfWay < currentPage) {
            return currentPage - halfWay + i;
        } else {
            return i;
        }
    } else {
        return i;
    }
} 
function addDefaultOption(list){
	var item = {};
	item._id ="-2"
	item.description = "-- Ingen --";
    list.unshift(item);
    return list;
}