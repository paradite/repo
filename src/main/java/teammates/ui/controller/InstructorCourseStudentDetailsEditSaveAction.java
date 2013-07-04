package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.GateKeeper;

public class InstructorCourseStudentDetailsEditSaveAction extends InstructorCoursePageAction {
	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Const.ParamsNames.STUDENT_EMAIL);
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		InstructorCourseStudentDetailsEditPageData data = new InstructorCourseStudentDetailsEditPageData(account);
		
		data.student = logic.getStudentForEmail(courseId, studentEmail);
		data.regKey = logic.getKeyForStudent(courseId, studentEmail);
		
		data.student.name = getRequestParam(Const.ParamsNames.STUDENT_NAME);
		data.student.email = getRequestParam(Const.ParamsNames.NEW_STUDENT_EMAIL);
		data.student.team = getRequestParam(Const.ParamsNames.TEAM_NAME);
		data.student.comments = getRequestParam(Const.ParamsNames.COMMENTS);	
		
		try {
			logic.updateStudent(studentEmail, data.student);
			statusToUser.add(Const.StatusMessages.STUDENT_EDITED);
			statusToAdmin = "Student <span class=\"bold\">" + data.student.email + 
					"'s</span> details in Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"+ 
					"New Email: " + data.student.email + "<br>New Team: " + 
					data.student.team + "<br>Comments: " + data.student.comments;
			
			RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS);
			result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
			return result;
			
		} catch (InvalidParametersException e) {
			isError = true;
			statusToUser.add(e.getMessage());
			statusToAdmin = Const.ACTION_RESULT_FAILURE+ " : " + e.getMessage();
			ShowPageResult result = createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_EDIT, data);
			return result;
		}
		
	}


}