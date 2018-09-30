var happeningController = angular.module("happeningController", [],
	function($compileProvider) {
		$compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file|javascript):/);
});

happeningController.controller("HappeningController", function($scope, $http, $filter, $routeParams) {
		$scope.expenseTypes = {};
		$scope.newHappening = {};
	 	$scope.selection = [];
		$scope.newHappening.start = "";
		$scope.newHappening.slutt = "";
		$scope.filtered = false;

		getExpenseDetails($scope, $http, $scope.selectedExpDetId);

		$scope.toggleSelection = function(ind){
			var idx = $scope.selection.indexOf(ind);

    	if (idx > -1) {// Is currently selected
      	$scope.selection.splice(idx, 1);
    	}
    	else { // Is newly selected
      	$scope.selection.push(ind);
    	}
		}
		$scope.add = function(formet){
			var responsePromise = $http.post("/expenseDetails/addHappening", {
				"happening" : {
					"name": formet.happeningName,
					"expType" : formet.expType,
					"purchases" : $scope.selection
				}
			}, {});
			responsePromise.success(function(dataFromServer, status, headers, config) {
				$scope.newHappening.expType = "";
				$scope.newHappening.start = "";
				$scope.newHappening.slutt = "";
				$scope.newHappening.happeningName = "";
				$scope.filtered = false;
			});
			responsePromise.error(function(data, status, headers, config) {
				alert("Legge til feilet!");
			});
		}

		$scope.getPurchases = function(){
			if(!$scope.newHappening.start){
				console.log("Start er påkrevd");
				return;
			}
			else if(!$scope.newHappening.slutt){
				console.log("Slutt er påkrevd");
				return;
			}
			else {
				populatePurchaseData1($scope, $http);
			}
		}
});

function getExpenseDetails($scope, $http, selectedExpDetId) {
	var url = '/expenseDetails/list';
	$http.get(url).success(function(data, status, headers, config) {
		$scope.expDetList = data.result.expDetList;
		$scope.expenseTypes = data.result.expTypesList;
	});
}

function populatePurchaseData1($scope, $http) {
	var url = "/purchases/listAll?start=" + $scope.newHappening.start + "&stop=" + $scope.newHappening.slutt;

	$http.get(url).success(function(data, status, headers, config) {
		$scope.purchasesList = data.purchasesList;
		$scope.filtered = true;
	});
}
