package com.easefun.polyvsdk.util;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.easefun.polyvsdk.PolyvSDKUtil;
import com.easefun.polyvsdk.sub.auxilliary.IListener;
import com.easefun.polyvsdk.sub.vlms.auxiliary.PolyvVlmsTransfer;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvAccessTokenInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvAddAnswerInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvAddOrderInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvAddQuestionInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvAnswerInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvCourseDetailInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvCoursesInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvCurriculumInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvQuestionInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvUserInfo;
import com.easefun.polyvsdk.sub.vlms.listener.PolyvVlmsApiListener;
import com.easefun.polyvsdk.sub.vlms.main.PolyvVlmsManager;
import com.easefun.polyvsdk.sub.vlms.main.PolyvVlmsTestData;
import com.easefun.polyvsdk.vo.PolyvVideoVO;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;

/**
 * 对应demo需求的网校接口管理类
 */
public class PolyvVlmsHelper {
    private static final int accessToken_validTime = 1000 * 60 * 60 * 2;
    private static String accessToken, user_id;
    private static long getAccessTokenTime;
    private PolyvVlmsManager vlmsManager;
    private List<Call> callLists;
    private List<Future> futureLists;
    private boolean canCall;

    public PolyvVlmsHelper() {
        canCall = true;
        vlmsManager = new PolyvVlmsManager(null);
        callLists = new ArrayList<>();
        futureLists = new ArrayList<>();
    }

    private boolean accessTokenIsInvalid() {
        return accessToken == null || (getAccessTokenTime != 0 && (System.currentTimeMillis() - getAccessTokenTime) > accessToken_validTime);
    }

    private void callFail(IListener listener, Throwable t) {
        if (listener != null && canCall)
            listener.fail(t);
    }

    private void callSuccess(IListener listener, Object o) {
        if (listener != null && canCall)
            listener.success(o);
    }

    /**
     * 销毁所有请求
     */
    public void destroy() {
        canCall = false;
        for (Call call : callLists) {
            if (call != null) {
                call.cancel();
            }
        }
        callLists.clear();
        for (Future future : futureLists) {
            if (future != null) {
                future.cancel(true);
            }
        }
        futureLists.clear();
    }

    private void getAccessToken(final IListener listener) {
        if (accessTokenIsInvalid()) {
            callLists.add(vlmsManager.getAccessToken(PolyvVlmsTestData.API_ID, PolyvVlmsTestData.SCHOOL_ID, PolyvVlmsTestData.APP_SECRET, new PolyvVlmsApiListener.GetAccessTokenListener() {
                @Override
                public void fail(Throwable t) {
                    callFail(listener, t);
                }

                @Override
                public void success(PolyvAccessTokenInfo polyvAccessTokenInfo) {
                    synchronized (vlmsManager) {
                        if (accessToken == null) {
                            accessToken = polyvAccessTokenInfo.data.access_token;
                            getAccessTokenTime = System.currentTimeMillis();
                        }
                        callSuccess(listener, null);
                    }
                }
            }));
        } else {
            callSuccess(listener, null);
        }
    }

    // 获取课程信息
    public void getCoursesInfo(final int page_size, final String is_free, final PolyvVlmsApiListener.GetCoursesListener coursesListener) {
        getAccessToken(new IListener() {
            @Override
            public void fail(Throwable t) {
                callFail(coursesListener, t);
            }

            @Override
            public void success(Object o) {
                callLists.add(vlmsManager.getCourses(PolyvVlmsTestData.SCHOOL_ID, null, is_free, null, 1, page_size, accessToken, coursesListener));
            }
        });
    }

    // 获取课程详情
    public void getCourseDetailInfo(final String courseId, final PolyvVlmsApiListener.GetCourseDetailListener detailListener) {
        getAccessToken(new IListener() {
            @Override
            public void fail(Throwable t) {
                callFail(detailListener, t);
            }

            @Override
            public void success(Object o) {
                callLists.add(vlmsManager.getCourseDetail(PolyvVlmsTestData.SCHOOL_ID, courseId, PolyvCourseDetailInfo.IS_DETAIL_YES, accessToken, detailListener));
            }
        });
    }

    // 根据界面需求获取课程及老师的信息
    public void getCoursesDetail(int page_size, String is_free, final GetCoursesDetailListener coursesDetailListener) {
        getCoursesInfo(page_size, is_free, new PolyvVlmsApiListener.GetCoursesListener() {
            @Override
            public void fail(Throwable t) {
                callFail(coursesDetailListener, t);
            }

            @Override
            public void success(final PolyvCoursesInfo polyvCoursesInfo) {
                final Map<Integer, CoursesDetail> maps = new TreeMap<Integer, CoursesDetail>();
                if (polyvCoursesInfo.data.courses.size() == 0) {
                    callSuccess(coursesDetailListener, new ArrayList<CoursesDetail>(maps.values()));
                } else {
                    int[] j = new int[]{1};
                    int i = 0;
                    for (int k = 0; k < polyvCoursesInfo.data.courses.size(); k++) {
                        // 非点播的课程视频不添加
                        if (!PolyvCoursesInfo.COURSE_TYPE_VOD.equals(polyvCoursesInfo.data.courses.get(k).course_type))
                            polyvCoursesInfo.data.courses.remove(polyvCoursesInfo.data.courses.get(k));
                    }
                    if (polyvCoursesInfo.data.courses.size() == 0) {
                        callSuccess(coursesDetailListener, new ArrayList<CoursesDetail>(maps.values()));
                        return;
                    }
                    if (canCall)
                        for (PolyvCoursesInfo.Course course : polyvCoursesInfo.data.courses) {
                            getCourseDetailInfo(course.course_id, new MyGetCourseDetailListener(coursesDetailListener, course, maps, polyvCoursesInfo.data.courses.size(), j, i++));
                        }
                }
            }
        });
    }

    private boolean isLogin() {
        return !TextUtils.isEmpty(user_id);
    }

    // 登录，获取user_id
    public void login(final IListener listener) {
        getAccessToken(new IListener() {
            @Override
            public void fail(Throwable t) {
                callFail(listener, t);
            }

            @Override
            public void success(Object o) {
                if (!isLogin())
                    callLists.add(vlmsManager.login(PolyvVlmsTestData.SCHOOL_ID, PolyvVlmsTestData.ACCOUNT, PolyvVlmsTestData.PASSWORD, accessToken, new PolyvVlmsApiListener.LoginListener() {
                        @Override
                        public void fail(Throwable t) {
                            callFail(listener, t);
                        }

                        @Override
                        public void success(PolyvUserInfo polyvUserInfo) {
                            user_id = polyvUserInfo.data.user_id;
                            callSuccess(listener, null);
                        }
                    }));
                else
                    callSuccess(listener, null);
            }
        });
    }

    // 为免费课程添加订单，注：已购买的课程，再次购买会回调失败。
    public void addOrder(final String course_id, final PolyvVlmsApiListener.AddOrderListener addOrderListener) {
        login(new PolyvVlmsApiListener.LoginListener() {
            @Override
            public void fail(Throwable t) {
                callFail(addOrderListener, t);
            }

            @Override
            public void success(PolyvUserInfo polyvUserInfo) {
                callLists.add(vlmsManager.addOrder(PolyvVlmsTestData.SCHOOL_ID, user_id, course_id, PolyvAddOrderInfo.PAYMENT_TYPE_FREE, accessToken, addOrderListener));
            }
        });
    }

    // 获取课时信息
    public void getCurriculum(final String course_id, final PolyvVlmsApiListener.GetCurriculumListener curriculumListener) {
        login(new PolyvVlmsApiListener.LoginListener() {
            @Override
            public void fail(Throwable t) {
                callFail(curriculumListener, t);
            }

            @Override
            public void success(PolyvUserInfo polyvUserInfo) {
                callLists.add(vlmsManager.getCurriculum(PolyvVlmsTestData.SCHOOL_ID, course_id, user_id, accessToken, curriculumListener));
            }
        });
    }

    // 根据界面需求获取课时信息
    public void getCurriculumDetail(String course_id, final MyCurriculumDetailListener curriculumDetailListener) {
        getCurriculum(course_id, new PolyvVlmsApiListener.GetCurriculumListener() {
            @Override
            public void fail(Throwable t) {
                callFail(curriculumDetailListener, t);
            }

            @Override
            public void success(PolyvCurriculumInfo polyvCurriculumInfo) {
                List<PolyvVlmsHelper.CurriculumsDetail> lists = new ArrayList<CurriculumsDetail>();
                if (polyvCurriculumInfo.data.sections.size() == 0) {
                    callSuccess(curriculumDetailListener, lists);
                } else {
                    if (canCall)
                        new LoadVideoVOTalk(curriculumDetailListener, polyvCurriculumInfo.data.sections, lists).getVideoVO();
                }
            }
        });
    }

    // 获取问题
    public void getQuestion(final String course_id, final PolyvVlmsApiListener.GetQuestionListener listener) {
        getAccessToken(new IListener() {
            @Override
            public void fail(Throwable t) {
                callFail(listener, t);
            }

            @Override
            public void success(Object o) {
                callLists.add(vlmsManager.getQuestion(PolyvVlmsTestData.SCHOOL_ID, course_id, accessToken, listener));
            }
        });
    }

    // 获取回复
    public void getAnswer(final String question_id, final PolyvVlmsApiListener.GetAnswerListener answerListener) {
        getAccessToken(new IListener() {
            @Override
            public void fail(Throwable t) {
                callFail(answerListener, t);
            }

            @Override
            public void success(Object o) {
                callLists.add(vlmsManager.getAnswer(PolyvVlmsTestData.SCHOOL_ID, question_id, accessToken, answerListener));
            }
        });
    }

    // 根据界面设计获取问题的详细
    public void getQuesionsDetail(String course_id, final MyQuestionDetailListener detailListener) {
        getQuestion(course_id, new PolyvVlmsApiListener.GetQuestionListener() {
            @Override
            public void fail(Throwable t) {
                callFail(detailListener, t);
            }

            @Override
            public void success(PolyvQuestionInfo polyvQuestionInfo) {
                Map<Integer, QuestionsDetail> maps = new TreeMap<Integer, QuestionsDetail>();
                if (polyvQuestionInfo.data.questions.size() == 0) {
                    callSuccess(detailListener, new ArrayList<>(maps.values()));
                } else {
                    int[] j = new int[]{1};
                    int i = 0;
                    if (canCall)
                        for (PolyvQuestionInfo.Question questoin : polyvQuestionInfo.data.questions) {
                            getAnswer(questoin.question_id, new MyGetAnswerListener(detailListener, questoin, maps, polyvQuestionInfo.data.questions.size(), j, i++));
                        }
                }
            }
        });
    }

    // 添加新问题，注：已购买的课程才能添加问题/回复
    public void addNewQuestion(final String course_id, final String title, final String content, final PolyvVlmsApiListener.AddNewQuestionListener questionListener) {
        getAccessToken(new IListener() {
            @Override
            public void fail(Throwable t) {
                callFail(questionListener, t);
            }

            @Override
            public void success(Object o) {
                callLists.add(vlmsManager.addNewQuestion(PolyvVlmsTestData.SCHOOL_ID, course_id, user_id, title, content, accessToken, questionListener));
            }
        });
    }

    // 添加新回复
    public void addNewAnswer(final String course_id, final String question_id, final String content, final PolyvVlmsApiListener.AddNewAnswerListener addNewAnswerListener) {
        getAccessToken(new IListener() {
            @Override
            public void fail(Throwable t) {
                callFail(addNewAnswerListener, t);
            }

            @Override
            public void success(Object o) {
                callLists.add(vlmsManager.addNewAnswer(PolyvVlmsTestData.SCHOOL_ID, course_id, question_id, user_id, content, accessToken, addNewAnswerListener));
            }
        });
    }

    // 添加问题获取的信息转为问题的详细信息
    public QuestionsDetail addQuestionInfoToQuestionDetail(PolyvAddQuestionInfo polyvAddQuestionInfo) {
        PolyvVlmsHelper.QuestionsDetail questionsDetail = new PolyvVlmsHelper.QuestionsDetail();
        PolyvQuestionInfo.Question question = new PolyvQuestionInfo.Question();
        question.nickname = polyvAddQuestionInfo.data.nickname;
        question.avatar = polyvAddQuestionInfo.data.avatar;
        // 转换为html字符
        question.content = PolyvVlmsTransfer.fromHtmlText(polyvAddQuestionInfo.data.content, false);
        question.course_id = polyvAddQuestionInfo.data.course_id;
        question.date_added = polyvAddQuestionInfo.data.date_added;
        question.last_modified = polyvAddQuestionInfo.data.last_modified;
        question.question_id = polyvAddQuestionInfo.data.question_id;
        question.school_id = polyvAddQuestionInfo.data.school_id;
        question.user_id = polyvAddQuestionInfo.data.user_id;
        question.title = polyvAddQuestionInfo.data.title;
        questionsDetail.question = question;
        questionsDetail.answers = new LinkedList<PolyvVlmsHelper.QuestionsDetail.AnswerDetail>();
        questionsDetail.content_display = new SpannableStringBuilder(question.nickname + " : ").append(question.content);
        return questionsDetail;
    }

    // 添加回复获取的信息转为回复的详细信息
    public QuestionsDetail.AnswerDetail addAnswerInfoToAnswerDetail(PolyvAddAnswerInfo addAnswerInfo) {
        QuestionsDetail.AnswerDetail answerDetail = new QuestionsDetail.AnswerDetail();
        PolyvAnswerInfo.Answer answer = new PolyvAnswerInfo.Answer();
        answer.nickname = addAnswerInfo.data.nickname;
        answer.answer_id = addAnswerInfo.data.answer_id;
        answer.avatar = addAnswerInfo.data.avatar;
        // 转换为html字符
        answer.content = PolyvVlmsTransfer.fromHtmlText(addAnswerInfo.data.content, false);
        answer.course_id = addAnswerInfo.data.course_id;
        answer.date_added = addAnswerInfo.data.date_added;
        answer.last_modified = addAnswerInfo.data.last_modified;
        answer.question_id = addAnswerInfo.data.question_id;
        answer.user_id = addAnswerInfo.data.user_id;
        answerDetail.answer = answer;
        answerDetail.content_display = new SpannableStringBuilder(answer.nickname + " : ").append(answer.content);
        return answerDetail;
    }

    public interface GetCoursesDetailListener extends IListener<List<CoursesDetail>> {
    }

    public interface MyCurriculumDetailListener extends IListener<List<CurriculumsDetail>> {
    }

    public interface MyQuestionDetailListener extends IListener<List<QuestionsDetail>> {
    }

    // 结合界面的需求将课程所需的信息整合在一起
    public static class CoursesDetail {
        public PolyvCoursesInfo.Course course;
        public PolyvCourseDetailInfo.Teacher teacher;

        @Override
        public String toString() {
            return "CoursesDetail{" +
                    "course=" + course +
                    ", teacher=" + teacher +
                    '}';
        }
    }

    // 结合demo界面的需求将课时所需的信息整合在一起
    public static class CurriculumsDetail {
        // 章节名称
        public String section_name;
        // 课时名称
        public String name;
        // 封面图
        public String cover_image;
        public PolyvVideoVO videoVO;
        public PolyvCurriculumInfo.Lecture lecture;

        @Override
        public String toString() {
            return "CurriculumDetail{" +
                    "section_name='" + section_name + '\'' +
                    ", name='" + name + '\'' +
                    ", cover_image='" + cover_image + '\'' +
                    ", videoVO=" + videoVO +
                    ", lecture=" + lecture +
                    '}';
        }
    }

    // 结合demo界面的需求将问题所需的信息整合在一起
    public static class QuestionsDetail {
        public PolyvQuestionInfo.Question question;
        public LinkedList<AnswerDetail> answers;
        // 需要设置到TextView中的内容
        public SpannableStringBuilder content_display;

        @Override
        public String toString() {
            return "QuestionsDetail{" +
                    "question=" + question +
                    ", answers=" + answers +
                    ", content_display=" + content_display +
                    '}';
        }

        public static class AnswerDetail {
            public PolyvAnswerInfo.Answer answer;
            public SpannableStringBuilder content_display;

            @Override
            public String toString() {
                return "AnswerDetail{" +
                        "answer=" + answer +
                        ", content_display=" + content_display +
                        '}';
            }
        }
    }

    public class LoadVideoVOTalk {
        private ExecutorService executorService;
        private MyCurriculumDetailListener curriculumDetailListener;
        private List<PolyvCurriculumInfo.Section> sections;
        private List<PolyvVlmsHelper.CurriculumsDetail> lists;

        public LoadVideoVOTalk(MyCurriculumDetailListener curriculumDetailListener, List<PolyvCurriculumInfo.Section> sections, List<CurriculumsDetail> lists) {
            this.curriculumDetailListener = curriculumDetailListener;
            this.sections = sections;
            this.lists = lists;
            executorService = Executors.newSingleThreadExecutor();
        }

        public void getVideoVO() {
            futureLists.add(executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < sections.size(); i++) {
                        String section_name = "章节" + (i + 1);
                        List<PolyvCurriculumInfo.Lecture> lectures = sections.get(i).lectures;
                        for (int j = 0; j < lectures.size(); j++) {
                            if (!canCall) {
                                executorService.shutdownNow();
                                executorService = null;
                                return;
                            }
                            // 非点播课时不添加
                            if (!PolyvCurriculumInfo.TYPE_VIDEO.equals(lectures.get(j).type))
                                continue;
                            // vid为空不添加
                            if (TextUtils.isEmpty(lectures.get(j).vid))
                                continue;
                            try {
                                PolyvVideoVO videoVO = PolyvSDKUtil.loadVideoJSON2Video(lectures.get(j).vid);
                                if (videoVO == null) {
                                    continue;
                                }
                                String name = "课时" + (j + 1);
                                String cover_image = videoVO.getFirstImage();
                                CurriculumsDetail curriculumsDetail = new CurriculumsDetail();
                                curriculumsDetail.videoVO = videoVO;
                                curriculumsDetail.section_name = section_name;
                                curriculumsDetail.name = name;
                                curriculumsDetail.cover_image = cover_image;
                                curriculumsDetail.lecture = lectures.get(j);
                                lists.add(curriculumsDetail);
                            } catch (JSONException e) {
                                // ignore 发生异常的不添加
                            }
                        }
                    }
                    callSuccess(curriculumDetailListener, lists);
                    executorService.shutdownNow();
                    executorService = null;
                }
            }));
        }
    }

    public class MyGetAnswerListener implements PolyvVlmsApiListener.GetAnswerListener {
        private MyQuestionDetailListener questionDetailListener;
        private PolyvQuestionInfo.Question question;
        private Map<Integer, QuestionsDetail> maps;
        private int coursesSize;
        private int[] j;
        private int index;

        public MyGetAnswerListener(MyQuestionDetailListener questionDetailListener, PolyvQuestionInfo.Question question, Map<Integer, QuestionsDetail> maps, int coursesSize, int[] j, int index) {
            this.questionDetailListener = questionDetailListener;
            this.question = question;
            this.maps = maps;
            this.coursesSize = coursesSize;
            this.j = j;
            this.index = index;
        }

        @Override
        public void fail(Throwable t) {
            synchronized (PolyvVlmsHelper.class) {
                if (questionDetailListener != null && j[0] == 1 && canCall)
                    questionDetailListener.fail(t);
                j[0] = 0;
            }
        }

        @Override
        public void success(PolyvAnswerInfo polyvAnswerInfo) {
            synchronized (PolyvVlmsHelper.class) {
                QuestionsDetail questionsDetail = new QuestionsDetail();
                questionsDetail.question = question;
                // 换行为html文本
                questionsDetail.question.content = PolyvVlmsTransfer.fromHtmlText(questionsDetail.question.content, false);
                LinkedList<QuestionsDetail.AnswerDetail> lists = new LinkedList<>();
                for (PolyvAnswerInfo.Answer answer : polyvAnswerInfo.data.answers) {
                    QuestionsDetail.AnswerDetail answerDetail = new QuestionsDetail.AnswerDetail();
                    answerDetail.answer = answer;
                    // 换行为html文本
                    answerDetail.answer.content = PolyvVlmsTransfer.fromHtmlText(answerDetail.answer.content, false);
                    answerDetail.content_display = new SpannableStringBuilder(answer.nickname + " : ").append(answer.content);
                    lists.add(answerDetail);
                }
                questionsDetail.answers = lists;
                questionsDetail.content_display = new SpannableStringBuilder(question.nickname + " : ").append(question.content);
                maps.put(index, questionsDetail);
                if (questionDetailListener != null && maps.size() == coursesSize && canCall)
                    questionDetailListener.success(new ArrayList<QuestionsDetail>(maps.values()));
            }
        }
    }

    public class MyGetCourseDetailListener implements PolyvVlmsApiListener.GetCourseDetailListener {
        private GetCoursesDetailListener getCoursesDetailListener;
        private PolyvCoursesInfo.Course course;
        private Map<Integer, CoursesDetail> maps;
        private int coursesSize;
        private int[] j;
        private int index;

        public MyGetCourseDetailListener(GetCoursesDetailListener getCoursesDetailListener, PolyvCoursesInfo.Course course, Map<Integer, CoursesDetail> maps, int coursesSize, int[] j, int index) {
            this.getCoursesDetailListener = getCoursesDetailListener;
            this.course = course;
            this.maps = maps;
            this.coursesSize = coursesSize;
            this.j = j;
            this.index = index;
        }

        @Override
        public void fail(Throwable t) {
            synchronized (PolyvVlmsHelper.class) {
                if (getCoursesDetailListener != null && j[0] == 1 && canCall)
                    getCoursesDetailListener.fail(t);
                j[0] = 0;
            }
        }

        @Override
        public void success(PolyvCourseDetailInfo polyvCourseDetailInfo) {
            synchronized (PolyvVlmsHelper.class) {
                CoursesDetail coursesDetail = new CoursesDetail();
                coursesDetail.course = course;
                // 转换为html文本
                coursesDetail.course.summary = PolyvVlmsTransfer.fromHtmlText(coursesDetail.course.summary, true);
                if (polyvCourseDetailInfo.data.teachers.size() == 0) {
                    coursesDetail.teacher = new PolyvCourseDetailInfo.Teacher();
                    coursesDetail.teacher.teacher_name = "default";
                } else
                    coursesDetail.teacher = polyvCourseDetailInfo.data.teachers.get(0);
                maps.put(index, coursesDetail);
                if (getCoursesDetailListener != null && maps.size() == coursesSize && canCall)
                    getCoursesDetailListener.success(new ArrayList<CoursesDetail>(maps.values()));
            }
        }
    }
}
