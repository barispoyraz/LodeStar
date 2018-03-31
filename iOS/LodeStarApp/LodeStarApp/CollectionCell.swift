//
//  SearchCell.swift
//  mrJitters
//
//  Created by Ryan Kotzebue on 6/7/16.
//  Copyright © 2016 Ryan Kotzebue. All rights reserved.
//

import UIKit



class CollectionCell: UICollectionViewCell {
    
    @IBOutlet weak var departureTimeOffsetLabel: UILabel!
    @IBOutlet weak var cellImage: UIImageView!
    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var originAirportNameLabel: UILabel!
    
    func displayContent(title: String, cellImage: UIImage) {
        self.title.text = title
        
        self.cellImage.image = cellImage
        
    }
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
}
