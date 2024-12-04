use std::{collections::HashMap, result::Result};

use super::node::{
    boolean::NodeBoolean, colour::NodeColour, numeric::NodeNumeric, Evaluate, Node, NodeConstructor,
};

pub trait Resolve {
    fn resolve(self, grammar: Grammar) -> Result<Box<Self>, &'static str>;
}

pub struct Grammar {
    pub branches: HashMap<String, Branch>,
    pub root: String,
}

pub struct Rule<T> {
    pub resolve: T,
    pub chances: usize,
}

pub enum Branch {
    Boolean(Vec<Rule<NodeBoolean>>),
    Numeric(Vec<Rule<NodeNumeric>>),
    Colour(Vec<Rule<NodeColour>>),
}

impl Grammar {
    pub fn generate(self, depth: usize) -> Result<Node, &'static str> {
        let 
    }
}
