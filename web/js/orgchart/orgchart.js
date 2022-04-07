/**
 * jQuery org-chart/tree plugin.
 *
 * Author: Khisamutdinov Radik
 *
 * Based on the work of Wes Nolte
 * (without drag and drops mode)
 * https://github.com/wesnolte/jOrgChart
 *
 * and based on the work of Dmitry Sinyavsky
 * (css - rules for vertical nodes)
 * http://habrahabr.ru/post/55753/
 *
 * Licensed under the MIT and GPL licenses.
 *
 */ 
(function ($) {
	// Extend jQuery for copy attributes
	$.fn.copyAttributes = function (elem) {
		$this = $(this);
		$.each($(elem).prop('attributes'), function () {
			if (this.name != 'class') 
				$this.attr(this.name, this.value);
		});
		return $this;
	};

	$.fn.jOrgChart = function (options) {
		var opts = $.extend({}, $.fn.jOrgChart.defaults, options);
		var $appendTo = $(opts.chartElement);

		// build the tree
		var $this = $(this);
		var $container = $("<div class='" + opts.chartClass + "'/>");
		
		if ($this.is("ul")) {
			buildNode($this.find("li:first"), $container, 0, opts);
		}
		else if ($this.is("li")) {
			buildNode($this, $container, 0, opts);
		}
		
		$appendTo.append($container);
	};

	// Option defaults
	$.fn.jOrgChart.defaults = {
		chartElement : 'body',
		depth      : -1,
		chartClass : "jOrgChart",
		nodeClicked: function ($node, type) {}
	};
	
	var nodeCount = 0;
	
	// Method that recursively builds the tree (horizontal type)
	function buildNode($node, $appendTo, level, opts) {
		var $table = $("<table cellpadding='0' cellspacing='0' border='0'/>");
		var $tbody = $("<tbody/>");

		// Construct the node container(s)
		var $nodeRow = $("<tr/>").addClass("node-cells");
		var $nodeCell = $("<td/>").addClass("node-cell").attr("colspan", 2);
		
		//
		var $childContainer = $node.children("ul:first");
		var isVerticalNodes = ($childContainer.attr('type') == 'vertical');		
		
		var $childNodes = $childContainer.children("li");
		var $childNodesCount = !isVerticalNodes ? $childNodes.length : 0;
		var $nodeDiv;
		var icoMinus = "web/img/orgchart.minus.png";
		var icoPlus = "web/img/orgchart.plus.png";

		if ($childNodesCount > 1) {
			$nodeCell.attr("colspan", $childNodesCount * 2);
		}
		
		// Draw the node
		// Get the contents - any markup except li and ul allowed
		var $nodeContent = $node.clone()
								.children("ul,li")
								.remove()
								.end()
								.html();
		$nodeContent = wrapContent($nodeContent);
								
		//Increments the node count which is used to link the source list and the org chart
		nodeCount++;
		$node.data("tree-node", nodeCount);
		$nodeDiv = $("<div>").addClass("node")
							.copyAttributes($node)
							.data("tree-node", nodeCount)
							.append($nodeContent);		
		$nodeCell.append($nodeDiv);		
		
		if (isVerticalNodes && $childNodes.length > 0) {
			$nodeDiv.addClass("vertical");
			var $verticalNodeDiv = $("<div>").addClass("multi-tree");			
			buildVerticalTree($childContainer, $verticalNodeDiv, opts);			
			$nodeDiv.after($verticalNodeDiv);
		}		

		if ($childNodesCount > 0) {
			// if it can be expanded then change the cursor
			$nodeCell.append('<div class="expandedTree"><img class="cover" src=' + icoMinus + '></div>');
		}
										 
		// Expand and contract nodes
		if ($childNodesCount > 0) {		
			$nodeDiv.next().children('img.cover').click(function () {
				var $this = $nodeDiv;
				var $tr = $this.closest("tr");
				if ($tr.hasClass('contracted')) {
					$tr.removeClass('contracted').addClass('expanded');
					$tr.nextAll("tr").css('display', '');
					$(this).attr('src', icoMinus);
					// Update the <li> appropriately so that if the tree redraws collapsed/non-collapsed nodes
					// maintain their appearance
					$node.removeClass('collapsed');
				} else {
					$tr.removeClass('expanded').addClass('contracted');
					$tr.nextAll("tr").css('display', 'none');
					$(this).attr('src', icoPlus);
					$node.addClass('collapsed');
				}
			});	
		}

		$nodeRow.append($nodeCell);
		$tbody.append($nodeRow);

		if ($childNodesCount > 0) {
			// recurse until leaves found (-1) or to the level specified
			if (opts.depth == -1 || (level + 1 < opts.depth)) { 
				var $downLineRow = $("<tr/>");
				var $downLineCell = $("<td/>").attr("colspan", $childNodesCount * 2);
				$downLineRow.append($downLineCell);

				// draw the connecting line from the parent node to the horizontal line 
				$downLine = $("<div></div>").addClass("line down");
				$downLineCell.append($downLine);
				$tbody.append($downLineRow);

				// Draw the horizontal lines
				var $linesRow = $("<tr/>");
				$childNodes.each(function () {
					var $left = $("<td>&nbsp;</td>").addClass("line left top");
					var $right = $("<td>&nbsp;</td>").addClass("line right top");
					$linesRow.append($left).append($right);
				});

				// horizontal line shouldn't extend beyond the first and last child branches
				$linesRow.find("td:first")
						.removeClass("top")
						.end()
						.find("td:last")
						.removeClass("top");

				$tbody.append($linesRow);
				var $childNodesRow = $("<tr/>");
				$childNodes.each(function() {
				   var $td = $("<td class='node-container'/>");
				   $td.attr("colspan", 2);
				   // recurse through children lists and items
				   buildNode($(this), $td, level + 1, opts);
				   $childNodesRow.append($td);
				});
			}
			$tbody.append($childNodesRow);
		}

		// any classes on the LI element get copied to the relevant node in the tree
		// apart from the special 'collapsed' class, which collapses the sub-tree at this point
		if ($node.attr('class') != undefined) {
			var classList = $node.attr('class').split(/\s+/);
			$.each(classList, function (index, item) {
				if (item == 'collapsed') {
					$nodeRow.nextAll('tr').css('display', 'none');
					$nodeRow.removeClass('expanded');
					$nodeRow.addClass('contracted');
					$nodeRow.find('img.cover').attr('src', icoPlus);
				} else {
					$nodeDiv.addClass(item);
				}
			});
		}

		$table.append($tbody);
		$appendTo.append($table);

		// node click handler
		$nodeDiv.click(function() {
			opts.nodeClicked.call(this, $(this), 'horizontal');
		});

		/* Prevent trees collapsing if a link inside a node is clicked */
		$nodeDiv.children('a').click(function (e) {
			e.stopPropagation();
		});
	}
	
	// Method that recursively builds the tree (vertical type)
	function buildVerticalTree($node, $appendTo, opts) {		
		if ($node.is("ul")) {
			var $childNodes = $node.children("li");
			var $ul = $("<ul>");
			
			if ($childNodes.length > 0) {
				$childNodes.each(function () {
					buildVerticalTree($(this), $ul, opts);
				});				
			}			
			
			$appendTo.append($ul);
		}
		else if ($node.is("li")) {
			var $ul = $node.children("ul:first");
			var $li = $node.hasClass('last') ? $("<li>").addClass('last') : $("<li>");
			var $nodeDiv;
			
			// Draw the node
			// Get the contents - any markup except li and ul allowed
			var $nodeContent = $node.clone()
									.children("ul,li")
									.remove()
									.end()
									.html();
			$nodeContent = wrapContent($nodeContent);
			
			//Increments the node count which is used to link the source list and the org chart
			nodeCount++;
			$node.data("tree-node", nodeCount);			
			$nodeDiv = $nodeContent.find('div.content:first');
			$nodeDiv.copyAttributes($node).data("tree-node", nodeCount);
			$li.append($nodeContent);
			
			if ($ul.length > 0) {
				buildVerticalTree($ul, $li, opts);				
			}
			
			$appendTo.append($li);
			
			// node click handler
			$nodeDiv.click(function() {
				opts.nodeClicked.call(this, $(this), 'vertical');
			});
		}
	}
	
	// wrap the contents in a special wrapper
	function wrapContent(content) {
		content = $.trim(content);
		var wrapper = $("<span>");
		var contentDiv = $("<div>").addClass("content").append(content);		
		wrapper.append(contentDiv);
		return wrapper;
	}
})(jQuery);
