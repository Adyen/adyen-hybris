ACC.pwdstrength = {

	bindAll: function ()
	{
		this.bindPStrength();
	},

	bindPStrength: function ()
	{
		$('.strength').pstrength({ verdicts: [ACC.pwdStrengthVeryWeak,
			ACC.pwdStrengthWeak,
			ACC.pwdStrengthMedium,
			ACC.pwdStrengthStrong,
			ACC.pwdStrengthVeryStrong],
			tooShort: ACC.pwdStrengthTooShortPwd,
			minCharText: ACC.pwdStrengthMinCharText });
	}

};

$(document).ready(function ()
{
	ACC.pwdstrength.bindAll();
});
