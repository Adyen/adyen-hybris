/* jQuery Password Strength Plugin (pstrength) - A jQuery plugin to provide accessibility functions
 * Author: Tane Piper (digitalspaghetti@gmail.com)
 * Website: http://digitalspaghetti.me.uk
 * Licensed under the MIT License: http://www.opensource.org/licenses/mit-license.php
 * This code uses a modified version of Steve Moitozo's algorithm (http://www.geekwisdom.com/dyn/passwdmeter)
 *
 * === Changelog ===
 * Version 1.2 (03/09/2007)
 * Added more options for colors and common words
 * Added common words checked to see if words like 'password' or 'qwerty' are being entered
 * Added minimum characters required for password
 * Re-worked scoring system to give better results
 *
 * Version 1.1 (20/08/2007)
 * Changed code to be more jQuery-like
 *
 * Version 1.0 (20/07/2007)
 * Initial version.
 */
(function($){
	$.extend($.fn, {
		pstrength : function(options) {
			var options = $.extend({
				verdicts:	["Very Weak","Weak","Medium","Strong","Very Strong"],
				colors: 	["#f00","#c06", "#f60","#3c0","#3f0"],
				scores: 	[10,15,30,40],
				minchar:	6,
				tooShort:   "Too Short!",
				minCharText: "Minimum length is %d characters"
			},options);
			return this.each(function(){
				var infoarea = $(this).attr('id');
				if(options.minchar>0)
				$(this).parent().after('<div class="pstrength-minchar" id="' + infoarea + '_minchar">' + options.minCharText.replace('%d', options.minchar) + '</div>');
				$(this).parent().after('<div class="pstrength-info" id="' + infoarea + '_text"></div>');
				$(this).parent().after('<div><div class="pstrength-bar" id="' + infoarea + '_bar" style="border: 1px solid white; font-size: 1px; height: 5px; width: 0px;"></div></div>');
				$(this).keyup(function(){
					$.fn.runPassword($(this).val(), infoarea, options);
				});
			});
		},
		runPassword : function (password, infoarea, options){
			// Check password
			nPerc = $.fn.checkPassword(password, options);
			// Get controls
	    	var ctlBar = "#" + infoarea + "_bar";
	    	var ctlText = "#" + infoarea + "_text";
			// Color and text
			if (nPerc < 0) {
				strColor = '#ccc';
				strText = options.tooShort;
				$(ctlBar).css({width: "5%"});
			}
			else if(nPerc <= options.scores[0])
			{
		   		strColor = options.colors[0];
				strText = options.verdicts[0];
				$(ctlBar).css({width: "10%"});
			}
			else if (nPerc > options.scores[0] && nPerc <= options.scores[1])
			{
		   		strColor = options.colors[1];
				strText = options.verdicts[1];
				$(ctlBar).css({width: "20%"});
			}
			else if (nPerc > options.scores[1] && nPerc <= options.scores[2])
			{
			   	strColor = options.colors[2];
				strText = options.verdicts[2];
				$(ctlBar).css({width: "40%"});
			}
			else if (nPerc > options.scores[2] && nPerc <= options.scores[3])
			{
			   	strColor = options.colors[3];
				strText = options.verdicts[3];
				$(ctlBar).css({width: "60%"});
			}
			else
			{
			   	strColor = options.colors[4];
				strText = options.verdicts[4];
				$(ctlBar).css({width: "80%"});
			}
			$(ctlBar).css({backgroundColor: strColor});
			$(ctlText).html("<span style='color: " + strColor + ";'>" + strText + "</span>");
		},
		checkPassword : function(password, options)
		{
			var intScore = 0;
			var strVerdict = options.verdicts[0];
			// PASSWORD LENGTH
			if (password.length < options.minchar)                         // Password too short
			{
				intScore = (intScore - 100)
			}
			else if (password.length >= options.minchar && password.length <= (options.minchar + 2)) // Password Short
			{
				intScore = (intScore + 6)
			}
			else if (password.length >= (options.minchar + 3) && password.length <= (options.minchar + 4))// Password Medium
			{
				intScore = (intScore + 12)
			}
			else if (password.length >= (options.minchar + 5))                    // Password Large
			{
				intScore = (intScore + 18)
			}
			if (password.match(/[a-z]/))                              // [verified] at least one lower case letter
			{
				intScore = (intScore + 1)
			}
			if (password.match(/[A-Z]/))                              // [verified] at least one upper case letter
			{
				intScore = (intScore + 5)
			}
			// NUMBERS
			if (password.match(/\d+/))                                 // [verified] at least one number
			{
				intScore = (intScore + 5)
			}
			if (password.match(/(.*[0-9].*[0-9].*[0-9])/))             // [verified] at least three numbers
			{
				intScore = (intScore + 7)
			}
			// SPECIAL CHAR
			if (password.match(/.[!,@,#,$,%,^,&,*,?,_,~]/))            // [verified] at least one special character
			{
				intScore = (intScore + 5)
			}
			// [verified] at least two special characters
			if (password.match(/(.*[!,@,#,$,%,^,&,*,?,_,~].*[!,@,#,$,%,^,&,*,?,_,~])/))
			{
				intScore = (intScore + 7)
			}
			// COMBOS
			if (password.match(/([a-z].*[A-Z])|([A-Z].*[a-z])/))        // [verified] both upper and lower case
			{
				intScore = (intScore + 2)
			}
			if (password.match(/([a-zA-Z])/) && password.match(/([0-9])/)) // [verified] both letters and numbers
			{
				intScore = (intScore + 3)
			}
		 	// [verified] letters, numbers, and special characters
			if (password.match(/([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])/))
			{
				intScore = (intScore + 3)
			}
			return intScore;
		}
	});
})(jQuery);