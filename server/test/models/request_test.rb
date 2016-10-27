require 'test_helper'

class RequestTest < ActiveSupport::TestCase
  test "check samplecompleted" do
    namey = users(:namey)
    testuser = users(:testuser)
    trans = transactions(:sampletrans)
    req = requests(:samplecompleted)

    assert_equal namey.id, req.user_id
    assert_equal trans.id, req.trans.id
    assert_equal testuser.id, req.trans.payee.id
    assert_equal namey.id, req.trans.payer.id
  end

  test "distance" do
    longA = -77.037852
    latA = 38.898556
    longB = -77.043934
    latB = 38.897147

    assert_equal 0.341, Request.distance(longA, latA, longB, latB).round(3)
  end

  test "check near" do
    completed = requests(:samplecompleted)
    open = requests(:sampleopen)
    past_due = requests(:openpastdue)

    # Ensure they take place at the sample place
    # and that one is open and one is closed
    assert_equal completed.long, open.long
    assert_equal completed.lat, open.lat
    assert_equal past_due.long, open.long
    assert_equal past_due.lat, open.lat
    assert_not_nil completed.trans
    assert_nil open.trans
    assert_nil past_due.trans

    requests = Request.openNear(1.51, 1.51, 5.0)

    assert requests.include? open
    assert_not requests.include? completed
    assert_not requests.include? past_due
  end

  test "check accepted" do
    accepted = requests(:sampleaccepted)
    testuser = users(:testuser)

    assert_equal testuser.id, accepted.actor.id
    assert accepted.accepted?
  end

  test "doEdit" do
    open = requests(:sampleopen)
    owner = users(:namey)
    otheruser = users(:testuser)

    assert_equal owner.id, open.user.id
    old_title = open.title
    new_title = "This is a new title"
    newer_title = "This is a newer title"
    assert_not_equal old_title, new_title

    # Check that editing as owning user works
    assert Request.doEdit(open.id, owner.id, {'title' => new_title })
    open.reload
    assert_equal new_title, open.title

    # Ensure editing as another user doesn't work
    assert_not_equal open.title, newer_title
    assert_not Request.doEdit(open.id, otheruser.id, {'title' => newer_title})
    open.reload
    assert_not_equal newer_title, open.title
  end

  test "handle accept" do
    open = requests(:sampleopen)
    otheruser = users(:testuser)

    assert_not_equal otheruser.id, open.user.id
    assert open.open?

    # Ensure owner cannot accept
    assert_not Request.handle_action('accept', open.id, open.user.id)
    open.reload
    assert_not open.accepted?

    # Ensure another user can accept
    assert Request.handle_action('accept', open.id, otheruser.id)
    open.reload
    assert open.accepted?

    # Accepted (cannot accept an already accepted request)
    assert open.accepted?
    assert_not Request.handle_action('accept', open.id, otheruser.id)

    # TODO Completed
    # TODO Paid
  end

  test "handle reject" do
    open = requests(:sampleopen)
    otheruser = users(:testuser)

    assert_not_equal otheruser.id, open.user.id
    assert open.open?

    # Ensure cannot reject open request
    assert_not Request.handle_action('reject', open.id, otheruser.id)
    open.reload
    assert open.open?
    assert_nil open.actor

    # Accept it first
    assert Request.handle_action('accept', open.id, otheruser.id)
    open.reload
    assert open.accepted?

    # Test Rejections
    # Ensure that not anyone can reject
    assert_not Request.handle_action('reject', open.id, open.user.id)
    open.reload
    assert open.accepted?

    # Ensure actor can reject
    assert Request.handle_action('reject', open.id, otheruser.id)
    open.reload
    assert open.open?
    assert_nil open.actor

    # TODO Completed
    # TODO Paid
  end

  test "handle complete" do
    open = requests(:sampleopen)
    otheruser = users(:testuser)

    assert_not_equal otheruser.id, open.user.id
    assert open.open?

    # Ensure cannot complete open request
    assert_not Request.handle_action('complete', open.id, otheruser.id)
    open.reload
    assert open.open?
    assert_nil open.actor

    # Accept it first
    assert Request.handle_action('accept', open.id, otheruser.id)
    open.reload
    assert open.accepted?

    # Ensure only actor can complete it
    assert_not Request.handle_action('complete', open.id, open.user.id)
    open.reload
    assert open.accepted?

    # Complete it
    assert Request.handle_action('complete', open.id, otheruser.id)
    open.reload
    assert open.completed?
    assert_equal otheruser.id, open.actor.id

    # TODO Paid
  end
end
