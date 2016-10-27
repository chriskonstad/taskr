class AddActorToRequest < ActiveRecord::Migration
  def change
    add_reference :requests, :actor, index: true, foreign_key: true
  end
end
