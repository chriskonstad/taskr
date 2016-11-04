class AddFbidToUser < ActiveRecord::Migration
  def change
    add_column :users, :fbid, :string
  end
end
